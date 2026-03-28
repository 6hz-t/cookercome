const BAIDU_MAP_AK = 'VhLF9CxroEWj5WcVVAWkZSC9HxOVgoJk'
const BAIDU_MAP_WEBGL_SRC = `https://api.map.baidu.com/api?type=webgl&v=1.0&ak=${BAIDU_MAP_AK}`
const DEFAULT_TIMEOUT = 15000
const DEFAULT_RETRY = 2
const POLL_INTERVAL = 100

let bmapLoadingPromise = null
let lastDebugInfo = null
let scriptLoaded = false

function wait(ms) {
  return new Promise((resolve) => setTimeout(resolve, ms))
}

function isBaiduMapScript(script) {
  return script && typeof script.src === 'string' && script.src.includes('api.map.baidu.com/api')
}

function getBaiduMapScripts() {
  return Array.from(document.querySelectorAll('script')).filter((script) => isBaiduMapScript(script))
}

function collectDebugInfo(extra = {}) {
  const scripts = getBaiduMapScripts()
  return {
    ...extra,
    timestamp: new Date().toISOString(),
    online: typeof navigator !== 'undefined' ? navigator.onLine : null,
    pageProtocol: typeof location !== 'undefined' ? location.protocol : '',
    pagePath: typeof location !== 'undefined' ? `${location.origin}${location.pathname}` : '',
    bmapObjectReady: Boolean(window.BMapGL),
    scriptCount: scripts.length,
    scripts: scripts.map((script, index) => ({
      index,
      src: script.src,
      async: Boolean(script.async),
      defer: Boolean(script.defer),
      readyState: script.readyState || '',
      managed: script.dataset?.bmapgl === 'true'
    }))
  }
}

function createLoadError(message, code, extra = {}) {
  const error = new Error(message)
  error.code = code
  error.debugInfo = collectDebugInfo({ code, ...extra })
  lastDebugInfo = error.debugInfo
  return error
}

function createManagedScript() {
  // 使用同步方式加载，不使用 async/defer，让百度地图能正常执行 document.write
  const script = document.createElement('script')
  script.type = 'text/javascript'
  script.async = false
  script.defer = false
  script.dataset.bmapgl = 'true'
  script.src = BAIDU_MAP_WEBGL_SRC
  console.log('[BMapLoader] Creating script:', script.src)
  return script
}

function waitForBMapObject({ script, timeoutMs, attempt, debugLabel }) {
  return new Promise((resolve, reject) => {
    if (window.BMapGL) {
      console.log('[BMapLoader] BMapGL already exists')
      resolve(window.BMapGL)
      return
    }

    let settled = false
    let timer = null
    let poller = null

    const cleanup = () => {
      if (timer) {
        clearTimeout(timer)
      }
      if (poller) {
        clearInterval(poller)
      }
      script.removeEventListener('load', onLoad)
      script.removeEventListener('error', onError)
    }

    const settle = (handler, payload) => {
      if (settled) return
      settled = true
      cleanup()
      handler(payload)
    }

    const onLoad = () => {
      console.log('[BMapLoader] Script loaded, checking BMapGL...')
      // 百度地图加载后需要等待一小段时间才能初始化 BMapGL 对象
      setTimeout(() => {
        if (window.BMapGL) {
          settle(resolve, window.BMapGL)
          return
        }
        settle(
          reject,
          createLoadError('BMapGL script loaded but BMapGL object not initialized', 'bmap_object_missing', {
            debugLabel,
            attempt,
            scriptSrc: script.src
          })
        )
      }, 50)
    }

    const onError = (event) => {
      console.error('[BMapLoader] Script load error:', event)
      settle(
        reject,
        createLoadError('BMapGL script load failed', 'script_error', {
          debugLabel,
          attempt,
          scriptSrc: script.src
        })
      )
    }

    timer = setTimeout(() => {
      console.error('[BMapLoader] Timeout waiting for BMapGL')
      settle(
        reject,
        createLoadError('BMapGL load timeout', 'timeout', {
          debugLabel,
          attempt,
          timeoutMs,
          scriptSrc: script.src
        })
      )
    }, timeoutMs)

    poller = setInterval(() => {
      if (window.BMapGL) {
        settle(resolve, window.BMapGL)
      }
    }, POLL_INTERVAL)

    script.addEventListener('load', onLoad)
    script.addEventListener('error', onError)
  })
}

async function loadOnce({ timeoutMs, forceReload, attempt, debugLabel }) {
  if (forceReload) {
    getBaiduMapScripts().forEach((script) => {
      script.remove()
    })
    scriptLoaded = false
  }

  // 如果已经加载过了，直接返回
  if (scriptLoaded && window.BMapGL) {
    return window.BMapGL
  }

  let script = document.querySelector('script[data-bmapgl="true"]')

  if (!script) {
    const existing = getBaiduMapScripts()
    if (existing.length > 0) {
      script = existing[existing.length - 1]
    }
  }

  if (!script) {
    script = createManagedScript()
    // 使用 appendChild 同步添加脚本
    document.head.appendChild(script)
  }

  return waitForBMapObject({ script, timeoutMs, attempt, debugLabel })
}

export function getBMapDebugInfo() {
  return lastDebugInfo || collectDebugInfo()
}

export async function loadBMapGL(options = {}) {
  if (typeof window === 'undefined') {
    return Promise.reject(createLoadError('window is not available, cannot load map', 'window_missing'))
  }

  if (window.BMapGL) {
    lastDebugInfo = collectDebugInfo({ code: 'already_loaded' })
    console.log('[BMapLoader] Already loaded, returning cached instance')
    return window.BMapGL
  }

  const retry = Number.isFinite(Number(options.retry)) ? Math.max(0, Number(options.retry)) : DEFAULT_RETRY
  const timeoutMs = Number.isFinite(Number(options.timeoutMs)) ? Math.max(3000, Number(options.timeoutMs)) : DEFAULT_TIMEOUT
  const forceReload = Boolean(options.forceReload)
  const debugLabel = String(options.debugLabel || '').trim()

  if (bmapLoadingPromise && !forceReload) {
    return bmapLoadingPromise
  }

  bmapLoadingPromise = (async () => {
    let lastError = null
    const totalAttempts = retry + 1

    for (let attempt = 1; attempt <= totalAttempts; attempt += 1) {
      try {
        const shouldForceReload = forceReload || attempt > 1
        const bmap = await loadOnce({
          timeoutMs,
          forceReload: shouldForceReload,
          attempt,
          debugLabel
        })
        scriptLoaded = true
        lastDebugInfo = collectDebugInfo({ code: 'loaded', attempt, debugLabel })
        console.log('[BMapLoader] Successfully loaded BMapGL')
        return bmap
      } catch (error) {
        lastError = error
        console.error('[BMapLoader] Load failed', error?.debugInfo || error)
        if (attempt < totalAttempts) {
          await wait(350)
        }
      }
    }

    throw (lastError || createLoadError('BMapGL load failed', 'unknown', { debugLabel }))
  })().finally(() => {
    bmapLoadingPromise = null
  })

  return bmapLoadingPromise
}
