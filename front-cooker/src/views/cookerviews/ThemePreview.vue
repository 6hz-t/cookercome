<template>
  <div class="theme-preview">
    <h1>厨师端配色方案预览</h1>
    <p class="desc">点击色卡可切换主题，当前使用：<strong>{{ currentTheme.name }}</strong></p>

    <div class="theme-cards">
      <div 
        v-for="theme in themes" 
        :key="theme.key"
        class="theme-card"
        :class="{ active: currentTheme.key === theme.key }"
        @click="switchTheme(theme)"
      >
        <div class="card-preview" :style="{ background: theme.colors['bg-soft'] }">
          <div class="preview-bar" :style="{ background: theme.colors.primary }"></div>
          <div class="preview-content">
            <div class="preview-text" :style="{ color: theme.colors['text-primary'] }">标题</div>
            <div class="preview-sub" :style="{ color: theme.colors['text-secondary'] }">副标题</div>
          </div>
          <div class="preview-btn" :style="{ background: theme.colors.accent }">按钮</div>
        </div>
        <div class="card-name">{{ theme.name }}</div>
        <div class="card-colors">
          <span 
            v-for="(color, name) in theme.colors" 
            :key="name"
            class="color-dot"
            :style="{ background: color }"
            :title="name"
          ></span>
        </div>
      </div>
    </div>

    <div class="current-theme-detail">
      <h2>当前主题详情</h2>
      <div class="color-grid">
        <div class="color-item" v-for="(color, name) in currentTheme.colors" :key="name">
          <div class="color-box" :style="{ background: color }"></div>
          <div class="color-info">
            <div class="color-name">{{ formatName(name) }}</div>
            <div class="color-value">{{ color }}</div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  name: 'ThemePreview',
  data() {
    return {
      currentThemeKey: 'classic-gray',
      themes: [
        {
          key: 'classic-gray',
          name: '经典灰',
          desc: '专业稳重，适合商务场景',
          colors: {
            'primary': '#4A4A4A',
            'primary-light': '#6B6B6B',
            'accent': '#8C8C8C',
            'accent-hover': '#6F6F6F',
            'bg-white': '#FFFFFF',
            'bg-soft': '#F5F5F5',
            'text-primary': '#333333',
            'text-secondary': '#999999',
            'border': '#EDEDED'
          }
        },
        {
          key: 'warm-beige',
          name: '暖米棕',
          desc: '温暖亲和，适合食品餐饮',
          colors: {
            'primary': '#E8C39E',
            'primary-light': '#F0D5B8',
            'accent': '#D97F4A',
            'accent-hover': '#C96A38',
            'bg-white': '#FFFFFF',
            'bg-soft': '#F7F5F2',
            'text-primary': '#4A443E',
            'text-secondary': '#9A9086',
            'border': '#EAE5E0'
          }
        },
        {
          key: 'fresh-mint',
          name: '薄荷绿',
          desc: '清新自然，健康环保',
          colors: {
            'primary': '#9ED2BE',
            'primary-light': '#B8E0D0',
            'accent': '#5A9367',
            'accent-hover': '#4A7F56',
            'bg-white': '#FFFFFF',
            'bg-soft': '#F2F8F5',
            'text-primary': '#3A4B3F',
            'text-secondary': '#7A8C80',
            'border': '#E6EEE9'
          }
        },
        {
          key: 'ocean-blue',
          name: '深海蓝',
          desc: '沉稳专业，值得信赖',
          colors: {
            'primary': '#2D5A7B',
            'primary-light': '#4A7FA3',
            'accent': '#4A90B8',
            'accent-hover': '#3A7FA3',
            'bg-white': '#FFFFFF',
            'bg-soft': '#F0F6FA',
            'text-primary': '#2C3E50',
            'text-secondary': '#7F8C8D',
            'border': '#E1E8ED'
          }
        },
        {
          key: 'sakura-pink',
          name: '樱花粉',
          desc: '温柔优雅，女性友好',
          colors: {
            'primary': '#D4A5A5',
            'primary-light': '#E0B8B8',
            'accent': '#C97B7B',
            'accent-hover': '#B86B6B',
            'bg-white': '#FFFFFF',
            'bg-soft': '#FAF5F5',
            'text-primary': '#4A3A3A',
            'text-secondary': '#9A8A8A',
            'border': '#EAE0E0'
          }
        },
        {
          key: 'lemon-yellow',
          name: '柠檬黄',
          desc: '活力明快，引人注目',
          colors: {
            'primary': '#E8D060',
            'primary-light': '#F0E080',
            'accent': '#D4A020',
            'accent-hover': '#C09018',
            'bg-white': '#FFFFFF',
            'bg-soft': '#FDFBF5',
            'text-primary': '#3D3420',
            'text-secondary': '#8C8060',
            'border': '#EAE5D0'
          }
        }
      ]
    }
  },
  computed: {
    currentTheme() {
      return this.themes.find(t => t.key === this.currentThemeKey) || this.themes[0]
    }
  },
  methods: {
    switchTheme(theme) {
      this.currentThemeKey = theme.key
      this.applyTheme(theme)
    },
    applyTheme(theme) {
      const root = document.documentElement
      for (const [key, value] of Object.entries(theme.colors)) {
        root.style.setProperty(`--color-${key}`, value)
      }
      // 保存到 localStorage
      localStorage.setItem('cooker-theme', theme.key)
    },
    formatName(name) {
      const names = {
        'primary': '主色',
        'primary-light': '主色 - 浅',
        'accent': '强调色',
        'accent-hover': '强调色 - 深',
        'bg-white': '背景 - 白',
        'bg-soft': '背景 - 柔',
        'text-primary': '文字 - 主',
        'text-secondary': '文字 - 次',
        'border': '边框'
      }
      return names[name] || name
    }
  },
  mounted() {
    // 加载保存的主题
    const saved = localStorage.getItem('cooker-theme')
    if (saved && this.themes.find(t => t.key === saved)) {
      this.currentThemeKey = saved
    }
    this.applyTheme(this.currentTheme)
  }
}
</script>

<style scoped>
.theme-preview {
  padding: 40px 20px;
  max-width: 1400px;
  margin: 0 auto;
}

h1 {
  text-align: center;
  font-size: 28px;
  margin-bottom: 12px;
  color: var(--color-text-primary);
}

.desc {
  text-align: center;
  color: var(--color-text-secondary);
  margin-bottom: 40px;
}

.theme-cards {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(280px, 1fr));
  gap: 24px;
  margin-bottom: 48px;
}

.theme-card {
  cursor: pointer;
  border-radius: 12px;
  overflow: hidden;
  border: 2px solid transparent;
  transition: all 0.3s;
}

.theme-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 8px 24px rgba(0,0,0,0.12);
}

.theme-card.active {
  border-color: var(--color-accent);
  box-shadow: 0 4px 16px rgba(0,0,0,0.15);
}

.card-preview {
  height: 160px;
  padding: 16px;
  border-radius: 8px;
  margin: 8px;
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.preview-bar {
  height: 8px;
  border-radius: 4px;
  width: 60%;
}

.preview-content {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.preview-text {
  font-size: 16px;
  font-weight: 600;
}

.preview-sub {
  font-size: 12px;
}

.preview-btn {
  height: 32px;
  border-radius: 6px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #fff;
  font-size: 12px;
  font-weight: 500;
}

.card-name {
  text-align: center;
  padding: 12px;
  font-size: 16px;
  font-weight: 600;
  color: var(--color-text-primary);
}

.card-colors {
  display: flex;
  justify-content: center;
  gap: 8px;
  padding: 0 16px 16px;
}

.color-dot {
  width: 24px;
  height: 24px;
  border-radius: 50%;
  border: 2px solid rgba(0,0,0,0.1);
}

.current-theme-detail {
  background: var(--color-bg-white);
  border-radius: 12px;
  padding: 24px;
  box-shadow: 0 2px 12px rgba(0,0,0,0.08);
}

.current-theme-detail h2 {
  font-size: 20px;
  margin-bottom: 20px;
  color: var(--color-text-primary);
}

.color-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
  gap: 16px;
}

.color-item {
  display: flex;
  gap: 12px;
  align-items: center;
}

.color-box {
  width: 60px;
  height: 60px;
  border-radius: 8px;
  border: 1px solid var(--color-border);
  flex-shrink: 0;
}

.color-info {
  flex: 1;
}

.color-name {
  font-size: 14px;
  color: var(--color-text-secondary);
  margin-bottom: 4px;
}

.color-value {
  font-family: 'Consolas', monospace;
  font-size: 13px;
  color: var(--color-text-primary);
}
</style>
