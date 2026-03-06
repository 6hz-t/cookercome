<template>
  <div class="home">
    <!-- 固定右上角头像 -->
    <div class="profile-avatar-fixed" @click="$router.push('/profile')">
      <el-badge :value="5" :hidden="notificationCount === 0" class="avatar-badge">
        <el-avatar :size="48" src="https://cube.elemecdn.com/0/88/03b0d39583f48206768a7534e55bcpng.png" />
      </el-badge>
      <div class="avatar-tooltip">个人中心</div>
    </div>

    <!-- 头部横幅 -->
    <div class="banner scroll-animate" data-direction="top">
      <h1>厨师上门服务</h1>
      <p>专业厨师上门，为您烹饪美味佳肴</p>
      <el-button type="primary" size="large" @click="$router.push('/chefs')">
        立即预约
      </el-button>
    </div>

    <!-- 服务特色 -->
    <div class="features scroll-animate" data-direction="bottom">
      <h2>我们的服务</h2>
      
      <!-- 平行四边形按钮行 -->
      <div class="parallelogram-row">
        <div 
          v-for="(feature, index) in features" 
          :key="feature.title"
          class="parallelogram-btn"
          :class="{ active: activeIndex === index }"
          @mouseenter="activeIndex = index"
        >
          <span class="btn-text">{{ feature.title }}</span>
        </div>
      </div>
      
      <!-- 详细介绍显示区域 -->
      <div class="detail-display">
        <transition name="fade" mode="out-in">
          <div :key="activeIndex" class="detail-content">
            <h3 class="detail-title">{{ features[activeIndex].title }}</h3>
            <p class="detail-subtitle">{{ features[activeIndex].shortDesc }}</p>
            <el-divider />
            <p class="detail-text" ref="detailTextRef">
              <span 
                v-for="(char, charIndex) in displayedText" 
                :key="charIndex"
                class="char"
                :style="{ animationDelay: `${charIndex * 0.05}s` }"
              >{{ char }}</span>
            </p>
          </div>
        </transition>
      </div>
    </div>

    <!-- 顶级大厨 -->
    <div class="top-chefs scroll-animate" data-direction="bottom">
      <h2>顶级大厨</h2>
      <div class="chef-showcase">
        <!-- 第一位大厨：左图右文 -->
        <div class="chef-highlight-item" @click="$router.push('/chef/1')">
          <div class="chef-photo">
            <img src="@/assets/images/banner-bg.jpeg" alt="大厨 1" />
            <div class="chef-badge">
              <el-icon><Star /></el-icon>
              <span>好评率第一</span>
            </div>
          </div>
          <div class="chef-info-content">
            <div class="bg-gradient"></div>
            <div class="corner-tr"></div>
            <div class="corner-bl"></div>
            <h3>测试 1</h3>
            <p class="chef-title">特级厨师 | 20 年经验</p>
            <p class="chef-desc">擅长川菜和粤菜融合，曾就职于五星级酒店。对食材挑选极为严格，坚持使用最新鲜的本地有机食材。招牌菜“水煮鱼”被誉为全城最佳，辣而不燥，麻而不苦。</p>
            <div class="chef-stats">
              <div class="stat-item">
                <span class="stat-value">99.8%</span>
                <span class="stat-label">好评率</span>
              </div>
              <div class="stat-item">
                <span class="stat-value">2580+</span>
                <span class="stat-label">服务次数</span>
              </div>
              <div class="stat-item">
                <span class="stat-value">¥398</span>
                <span class="stat-label">起步价</span>
              </div>
            </div>
          </div>
        </div>

        <!-- 第二位大厨：左文右图 -->
        <div class="chef-highlight-item" @click="$router.push('/chef/2')">
          <div class="chef-info-content">
            <div class="bg-gradient"></div>
            <div class="corner-tr"></div>
            <div class="corner-bl"></div>
            <h3>测试 2</h3>
            <p class="chef-title">高级营养师 | 18 年经验</p>
            <p class="chef-desc">专注健康养生菜品研发，擅长根据客户体质定制营养菜单。精通药膳调理，将中医理论与现代营养学完美结合。独创的“四季养生宴”深受高端客户喜爱。</p>
            <div class="chef-stats">
              <div class="stat-item ">
                <span class="stat-value">95%</span>
                <span class="stat-label">回头客</span>
              </div>
              <div class="stat-item">
                <span class="stat-value">1860+</span>
                <span class="stat-label">服务次数</span>
              </div>
              <div class="stat-item">
                <span class="stat-value">¥458</span>
                <span class="stat-label">起步价</span>
              </div>
            </div>
          </div>
          <div class="chef-photo">
            <img src="@/assets/images/banner-bg.jpeg" alt="大厨 2" />
            <div class="chef-badge badge-gold">
              <el-icon><Trophy /></el-icon>
              <span>回头客第一</span>
            </div>
          </div>
        </div>

        <!-- 第三位大厨：左图右文 -->
        <div class="chef-highlight-item" @click="$router.push('/chef/3')">
          <div class="chef-photo">
            <img src="@/assets/images/banner-bg.jpeg" alt="大厨 3" />
            <div class="chef-badge badge-silver">
              <el-icon><Medal /></el-icon>
              <span>创意菜品奖</span>
            </div>
          </div>
          <div class="chef-info-content">
            <div class="bg-gradient"></div>
            <div class="corner-tr"></div>
            <div class="corner-bl"></div>
            <h3>测试 3</h3>
            <p class="chef-title">创新菜大师 | 15 年经验</p>
            <p class="chef-desc">致力于传统菜品的创新改良，将分子料理技术融入中餐烹饪。擅长打造视觉与味觉的双重盛宴，每一道菜都是艺术品。代表作品“荷塘月色”获全国烹饪大赛金奖。</p>
            <div class="chef-stats">
              <div class="stat-item">
                <span class="stat-value">98.5%</span>
                <span class="stat-label">好评率</span>
              </div>
              <div class="stat-item">
                <span class="stat-value">1520+</span>
                <span class="stat-label">服务次数</span>
              </div>
              <div class="stat-item">
                <span class="stat-value">¥368</span>
                <span class="stat-label">起步价</span>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- 推荐厨师 -->
    <div class="recommended-chefs scroll-animate" data-direction="bottom">
      <h2>推荐厨师</h2>
      <p class="section-subtitle">精选优质厨师，为您提供专业服务</p>
      
      <div class="chef-grid">
        <div 
          v-for="(chef, index) in recommendedChefs" 
          :key="chef.id"
          class="chef-card"
          :style="{ animationDelay: `${index * 0.1}s` }"
          @click="$router.push(`/chef/${chef.id}`)"
        >
          <div class="chef-avatar">
            <el-avatar :size="90" :src="chef.avatar" />
            <div class="chef-level-badge" :class="chef.levelClass">
              {{ chef.level }}
            </div>
          </div>
          
          <h4 class="chef-name">{{ chef.name }}</h4>
          
          <div class="chef-tags">
            <span 
              v-for="(tag, tagIndex) in chef.skills" 
              :key="tagIndex"
              class="skill-tag"
            >
              {{ tag }}
            </span>
          </div>
          
          <div class="chef-rating">
            <el-icon class="star-icon"><Star /></el-icon>
            <span class="rating-value">{{ chef.rating }}</span>
            <span class="rating-count">({{ chef.reviewCount }}条评价)</span>
          </div>
          
          <div class="chef-service-count">
            <el-icon><User /></el-icon>
            <span>已服务 {{ chef.serviceCount }}+ 次</span>
          </div>
          
          <div class="chef-price">
            <span class="price-label">起步价</span>
            <span class="price-value">¥{{ chef.price }}</span>
          </div>
          
          <el-button type="primary" round size="default" class="view-detail-btn">
            查看详情
            <el-icon><ArrowRight /></el-icon>
          </el-button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, watch, onMounted } from 'vue'
import {Medal, Star, Trophy, User, ArrowRight, Bell} from "@element-plus/icons-vue"

const notificationCount = ref(5) // 模拟未读消息数;

const recommendedChefs = [
  {
    id: 1,
    name: '王师傅',
    avatar: 'https://cube.elemecdn.com/0/88/03b0d39583f48206768a7534e55bcpng.png',
    level: '特级',
    levelClass: 'level-gold',
    skills: ['川菜', '湘菜', '家常菜'],
    rating: 4.9,
    reviewCount: 2580,
    serviceCount: 2580,
    price: 398
  },
  {
    id: 2,
    name: '李师傅',
    avatar: 'https://cube.elemecdn.com/0/88/03b0d39583f48206768a7534e55bcpng.png',
    level: '高级',
    levelClass: 'level-silver',
    skills: ['粤菜', '海鲜', '营养餐'],
    rating: 4.8,
    reviewCount: 1860,
    serviceCount: 1860,
    price: 458
  },
  {
    id: 3,
    name: '张师傅',
    avatar: 'https://cube.elemecdn.com/0/88/03b0d39583f48206768a7534e55bcpng.png',
    level: '特级',
    levelClass: 'level-gold',
    skills: ['鲁菜', '面食', '宴席菜'],
    rating: 4.9,
    reviewCount: 1520,
    serviceCount: 1520,
    price: 368
  },
  {
    id: 4,
    name: '刘师傅',
    avatar: 'https://cube.elemecdn.com/0/88/03b0d39583f48206768a7534e55bcpng.png',
    level: '中级',
    levelClass: 'level-bronze',
    skills: ['川菜', '火锅', '烧烤'],
    rating: 4.7,
    reviewCount: 980,
    serviceCount: 980,
    price: 298
  },
  {
    id: 5,
    name: '陈师傅',
    avatar: 'https://cube.elemecdn.com/0/88/03b0d39583f48206768a7534e55bcpng.png',
    level: '高级',
    levelClass: 'level-silver',
    skills: ['淮扬菜', '杭帮菜', '素食'],
    rating: 4.8,
    reviewCount: 1350,
    serviceCount: 1350,
    price: 428
  },
  {
    id: 6,
    name: '赵师傅',
    avatar: 'https://cube.elemecdn.com/0/88/03b0d39583f48206768a7534e55bcpng.png',
    level: '特级',
    levelClass: 'level-gold',
    skills: ['闽菜', '药膳', '养生餐'],
    rating: 4.9,
    reviewCount: 1680,
    serviceCount: 1680,
    price: 488
  },
  {
    id: 7,
    name: '黄师傅',
    avatar: 'https://cube.elemecdn.com/0/88/03b0d39583f48206768a7534e55bcpng.png',
    level: '中级',
    levelClass: 'level-bronze',
    skills: ['东北菜', '炖菜', '饺子'],
    rating: 4.7,
    reviewCount: 890,
    serviceCount: 890,
    price: 268
  },
  {
    id: 8,
    name: '周师傅',
    avatar: 'https://cube.elemecdn.com/0/88/03b0d39583f48206768a7534e55bcpng.png',
    level: '高级',
    levelClass: 'level-silver',
    skills: ['西餐', '日料', '融合菜'],
    rating: 4.8,
    reviewCount: 1120,
    serviceCount: 1120,
    price: 528
  }
]

const features = [
  { 
    icon: 'User', 
    title: '专业认证', 
    shortDesc: '严格审核，持证上岗',
    detail: '所有厨师均经过严格的身份验证、健康检查和专业技能考核。我们要求每位厨师必须持有国家认可的厨师资格证书和有效的健康证明，确保服务质量和食品安全。平台还会定期进行复审和培训，保证厨师团队的专业水准。通过背景调查、试菜评审、客户评价等多维度考核，只有最优秀的厨师才能在平台上为您提供服务。'
  },
  { 
    icon: 'Cooking', 
    title: '定制菜品', 
    shortDesc: '按需定制，专属美味',
    detail: '根据您的口味偏好、饮食禁忌和营养需求，为您量身定制专属菜单。无论是家常菜、地方特色菜，还是高端宴席菜，我们的厨师都能满足您的需求。您可以在预约时详细说明您的要求，包括辣度、咸淡、食材偏好等细节，厨师会根据您的反馈调整烹饪方案。从一人食到家庭聚餐，从生日宴到商务宴请，各种场合的用餐需求都能得到完美解决。'
  },
  { 
    icon: 'Location', 
    title: '就近匹配', 
    shortDesc: '智能推荐，快速上门',
    detail: '基于 LBS 地理位置智能匹配附近优质厨师，系统会自动筛选距离您最近、评分最高、最符合您需求的厨师资源。通过大数据分析和智能算法，我们能够在短时间内为您推荐最合适的厨师，最短 30 分钟即可上门为您服务。无论您是在家中、办公室还是其他场所，只要在城市服务范围内，都能享受到便捷的上门烹饪服务，让您足不出户就能品尝到美味佳肴。'
  },
  { 
    icon: 'Timer', 
    title: '准时上门', 
    shortDesc: '守时守信，绝不拖延',
    detail: '我们深知时间的宝贵，因此严格要求所有厨师遵守预约时间。厨师会提前规划最优路线，预留充足的路程时间，确保准时到达您的指定地点。如遇交通堵塞等不可抗力因素，厨师会第一时间与您沟通，说明情况并征得您的同意。若因厨师原因导致严重延误，平台将提供相应的补偿方案，包括但不限于费用减免、优惠券发放等，以表达我们的歉意。'
  },
  { 
    icon: 'Star', 
    title: '品质保证', 
    shortDesc: '不满意，全额退款',
    detail: '我们对服务质量有绝对的信心，同时也为您提供完善的售后保障。如果您对菜品口味、服务质量或整体体验有任何不满意，可以在服务结束后 24 小时内向平台反馈。我们会认真听取您的意见，并根据实际情况提供部分退款或全额退款的解决方案。您的满意度是我们前进的动力，我们承诺每一位消费者的合法权益都会得到充分保护，让您放心消费、安心享受服务。'
  },
  { 
    icon: 'Money', 
    title: '透明消费', 
    shortDesc: '明码标价，无隐形消费',
    detail: '平台所有服务费用均公开透明，在预约页面就会清晰展示各项费用明细，包括厨师服务费、食材费、交通费等。您在确认订单前可以清楚了解每一笔支出的用途，不存在任何隐形消费和强制消费。食材采购会提供详细小票，剩余食材可带走或委托厨师处理。平台还会定期推出优惠活动和会员折扣，让您在享受优质服务的同时也能获得实惠的价格。'
  },
  { 
    icon: 'Safety', 
    title: '安全保障', 
    shortDesc: '全程保险，安全无忧',
    detail: '为每一笔订单购买食品安全责任险和人身意外伤害险，双重保障让您的用餐更加安心。平台采用担保交易模式，您的支付金额会暂时托管在平台，待服务完成并确认满意后才会支付给厨师。厨房用具和食材都经过严格消毒和检验，确保卫生安全。厨师上门服务全程录音录像（经用户同意），既保障双方权益，也便于处理可能的纠纷。个人信息严格保密，不会泄露给第三方。'
  },
  { 
    icon: 'Service', 
    title: '贴心服务', 
    shortDesc: '一对一客服，随时响应',
    detail: '配备专业的客户服务团队，提供一对一专属服务顾问。从您注册账号开始，无论是咨询问题、选择菜品、预约厨师，还是售后服务、投诉建议，都有专人为您解答和处理。客服团队 7×12 小时在线响应（早 8 点至晚 8 点），工作日平均响应时间不超过 5 分钟。对于紧急需求或特殊要求，我们也会尽力协调资源，满足您的个性化需求。您的每一次反馈都会被认真对待，帮助我们不断改进服务质量。'
  }
]

const activeIndex = ref(0)
const displayedText = ref('')
const detailTextRef = ref(null)
let animationFrameId = null // 用于存储 RAF ID

// 监听 activeIndex 变化，实现逐字显示效果
watch(activeIndex, (newIndex) => {
  const text = features[newIndex].detail
  displayedText.value = ''
  
  // 取消之前的动画（如果还在进行中）
  if (animationFrameId) {
    cancelAnimationFrame(animationFrameId)
  }
  
  // 使用 requestAnimationFrame 实现流畅的逐字动画
  let charIndex = 0
  const typeWriter = () => {
    if (charIndex < text.length) {
      displayedText.value += text[charIndex]
      charIndex++
      animationFrameId = requestAnimationFrame(typeWriter)
    } else {
      animationFrameId = null // 动画完成，重置 ID
    }
  }
  
  // 立即开始打字效果，不延迟
  typeWriter()
}, { immediate: true })

// 滚动渐入动画 - IntersectionObserver
onMounted(() => {
  const observerOptions = {
    root: null,
    rootMargin: '-10% 0px -10% 0px', // 在元素进入视口 10% 时触发
    threshold: 0
  }

  const observer = new IntersectionObserver((entries) => {
    entries.forEach(entry => {
      if (entry.isIntersecting) {
        // 元素进入视口，添加激活类
        entry.target.classList.add('animate-active')
      } else {
        // 元素离开视口，移除激活类（可选，如果希望只播放一次动画则删除这行）
        entry.target.classList.remove('animate-active')
      }
    })
  }, observerOptions)

  // 监听所有需要动画的元素
  document.querySelectorAll('.scroll-animate').forEach(section => {
    observer.observe(section)
  })
})
</script>

<style scoped>
.home {
  width: 100%;
  min-height: 100vh;
  position: relative;
}

/* 固定右上角头像 */
.profile-avatar-fixed {
  position: fixed;
  top: 20px;
  right: 30px;
  z-index: 1000;
  cursor: pointer;
  transition: all 0.3s ease;
}

.profile-avatar-fixed:hover {
  transform: scale(1.1);
}

.profile-avatar-fixed:hover .avatar-tooltip {
  opacity: 1;
  visibility: visible;
  transform: translateY(-5px);
}

.avatar-badge {
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.15);
}

.avatar-tooltip {
  position: absolute;
  top: 100%;
  right: 0;
  margin-top: 8px;
  background: rgba(0, 0, 0, 0.8);
  color: white;
  padding: 6px 12px;
  border-radius: 6px;
  font-size: 13px;
  white-space: nowrap;
  opacity: 0;
  visibility: hidden;
  transform: translateY(-5px);
  transition: all 0.3s ease;
  pointer-events: none;
}

.banner {
  /* 背景图片和颜色设置 */
  background-color: rgba(53, 52, 52, 0.6); /* 后备背景颜色（图片加载失败时显示） */
  background-image: linear-gradient(rgba(0, 0, 0, 0.1), rgb(255, 255, 255)), url('@/assets/images/banner-bg.jpeg');
  background-size: cover; /* 覆盖整个区域，可选：cover/contain/auto */
  background-position: center center; /* 调整图片位置：center/center top/center bottom/left center/right center 等 */
  background-repeat: no-repeat; /* 不重复 */
  background-attachment: scroll; /* 固定或滚动：scroll/fixed */
  
  color: white;
  padding: 80px 20px;
  text-align: center;
  width: 100%;
}

.banner h1 {
  font-size: 48px;
  margin-bottom: 20px;
}

.banner p {
  font-size: 20px;
  margin-bottom: 40px;
}

.features {
  padding: 20px 20px;
  width: 100%;
  max-width: 1400px;
  margin: 0 auto;
}

.features h2 {
  text-align: center;
  margin-bottom: 50px;
  font-size: 32px;
  color: #333;
}

/* 平行四边形按钮行 */
.parallelogram-row {
  display: flex;
  justify-content: center;
  gap: 5px;
  margin-bottom: 40px;
  flex-wrap: wrap;
}

.parallelogram-btn {
  position: relative;
  width: 160px;
  height: 60px;
  background: linear-gradient(135deg, #f5f7fa 0%, #c3cfe2 100%);
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  transform: skewX(-20deg); /* 平行四边形效果 */
  transition: all 0.4s cubic-bezier(0.4, 0, 0.2, 1);
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1);
  overflow: hidden;
}

.parallelogram-btn::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 0;
  background: linear-gradient(135deg, rgba(0, 0, 0, 0.8) 0%, rgba(255, 255, 255, 0.4) 100%);
  transition: height 0.4s cubic-bezier(0.4, 0, 0.2, 1);
  z-index: 0;
}

.parallelogram-btn.active::before {
  height: 100%;
}

.btn-text {
  position: relative;
  z-index: 1;
  font-size: 16px;
  font-weight: 600;
  color: #333;
  transition: color 0.4s ease;
  white-space: nowrap;
}

.parallelogram-btn.active .btn-text {
  color: white;
}

.parallelogram-btn:hover {
  transform: skewX(-20deg) translateY(-5px);
  box-shadow: 0 8px 16px rgba(0, 0, 0, 0.2);
}

/* 详细介绍显示区域 */
.detail-display {
  min-height: 250px;
  padding: 10px 20px;
  position: relative;
}

.detail-content {
  text-align: center;
  animation: fadeIn 0.5s ease;
}

@keyframes fadeIn {
  from {
    opacity: 0;
    transform: translateY(10px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

.detail-title {
  font-size: 28px;
  font-weight: bold;
  color: #333;
  margin-bottom: 10px;
}

.detail-subtitle {
  font-size: 16px;
  color: #667eea;
  margin-bottom: 20px;
  font-weight: 500;
}

.detail-text {
  font-size: 16px;
  line-height: 1.8;
  color: #555;
  text-align: left;
  padding: 0 40px;
  min-height: 100px;
}

.char {
  display: inline-block;
  opacity: 0;
  animation: charAppear 0.3s ease forwards;
}

@keyframes charAppear {
  from {
    opacity: 0;
    transform: translateY(5px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

/* Fade transition */
.fade-enter-active,
.fade-leave-active {
  transition: opacity 0.3s ease;
}

.fade-enter-from,
.fade-leave-to {
  opacity: 0;
}

.recommended-chefs {
  padding: 60px 20px;
  width: 100%;
  max-width: 1400px;
  margin: 0 auto;
}

.recommended-chefs h2 {
  text-align: center;
  font-size: 36px;
  color: #333;
  font-weight: bold;
  margin-bottom: 10px;
}

.section-subtitle {
  text-align: center;
  font-size: 16px;
  color: #666;
  margin-bottom: 50px;
}

/* 顶级大厨模块样式 */
.top-chefs {
  padding: 60px 20px;
  width: 100%;
  max-width: 1400px;
  margin: 0 auto;
}

.top-chefs h2 {
  text-align: center;
  margin-bottom: 50px;
  font-size: 36px;
  color: #333;
  font-weight: bold;
}

.chef-showcase {
  display: flex;
  flex-direction: column;
  gap: 40px;
}

.chef-highlight-item {
  display: flex;
  align-items: center;
  gap: 30px;
  position: relative;
  transition: all 0.4s ease;
  cursor: pointer;
}

/* 图片区域 */
.chef-photo {
  position: relative;
  flex: 0 0 35%;
  overflow: hidden;
  border-radius: 8px;
}

/* 介绍区域 - 科技感四角边框 */
.chef-info-content {
  flex: 1;
  padding: 0;
  display: flex;
  flex-direction: column;
  justify-content: center;
  position: relative;
  width: 100%;
  align-self: center;
}

/* 背景渐变层 - 鼠标移入时从左到右显示 */
.chef-info-content .bg-gradient {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: linear-gradient(90deg, rgba(0, 210, 255, 0.15) 0%, rgba(0, 119, 255, 0.15) 100%);
  transform: scaleX(0);
  transform-origin: left;
  transition: transform 1s ease;
  z-index: 0;
  pointer-events: none;
}


/* 四个角的边框 */
.chef-info-content::before,
.chef-info-content::after {
  content: '';
  position: absolute;
  width: 15px;
  height: 15px;
  border-color: #667eea;
  border-style: solid;
  border-width: 2px;
  transition: all 0.3s ease;
}

/* 左上角和右下角 */
.chef-info-content::before {
  top: -5px;
  left: -5px;
  border-right: none;
  border-bottom: none;
}

.chef-info-content::after {
  bottom: -5px;
  right: -5px;
  border-left: none;
  border-top: none;
}

/* 右上角和左下角 - 使用额外元素 */
.chef-info-content .corner-tr,
.chef-info-content .corner-bl {
  position: absolute;
  width: 15px;
  height: 15px;
  border-color: #667eea;
  border-style: solid;
  border-width: 2px;
  transition: all 0.3s ease;
}

.chef-info-content .corner-tr {
  top: -5px;
  right: -5px;
  border-left: none;
  border-bottom: none;
}

.chef-info-content .corner-bl {
  bottom: -5px;
  left: -5px;
  border-right: none;
  border-top: none;
}

.chef-highlight-item:hover .chef-info-content .bg-gradient {
  transform: scaleX(1);
}

.chef-highlight-item:hover .chef-info-content::before,
.chef-highlight-item:hover .chef-info-content::after,
.chef-highlight-item:hover .corner-tr,
.chef-highlight-item:hover .corner-bl {
  width: 18px;
  height: 18px;
  border-color: #764ba2;
}

.chef-highlight-item.reverse {
  flex-direction: row-reverse;
}

.chef-photo {
  position: relative;
  flex: 0 0 40%;
  overflow: hidden;
}

.chef-photo img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  transition: transform 0.4s ease;
}

.chef-highlight-item:hover .chef-photo img {
  transform: scale(1.05);
}

.chef-badge {
  position: absolute;
  top: 20px;
  left: 20px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  padding: 10px 20px;
  border-radius: 20px;
  font-size: 14px;
  font-weight: 600;
  display: flex;
  align-items: center;
  gap: 8px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.2);
}

.chef-badge.badge-gold {
  background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%);
}

.chef-badge.badge-silver {
  background: linear-gradient(135deg, #4facfe 0%, #00f2fe 100%);
}

.chef-info-content h3 {
  font-size: 22px;
  color: #333;
  margin-bottom: 8px;
  font-weight: bold;
}

.chef-title {
  font-size: 14px;
  color: #667eea;
  margin-bottom: 15px;
  font-weight: 500;
}

.chef-desc {
  font-size: 13px;
  width: 98%;
  color: #666;
  line-height: 1.6;
  margin-bottom: 15px;
  text-align: justify;
  display: -webkit-box;
  -webkit-line-clamp: 3;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.chef-stats {
  display: flex;
  gap: 20px;
  margin-bottom: 0;
}

.stat-item {
  text-align: center;
}

.stat-value {
  display: block;
  font-size: 18px;
  font-weight: bold;
  color: #667eea;
  margin-bottom: 4px;
}

.stat-label {
  font-size: 12px;
  color: #999;
}

.chef-info-content .el-button {
  display: none;
}

.chef-info-content .el-button {
  align-self: flex-start;
  padding: 12px 30px;
  font-size: 16px;
  display: flex;
  align-items: center;
  gap: 8px;
}

.chef-info-content .el-button .el-icon {
  transition: transform 0.3s ease;
}

.chef-info-content .el-button:hover .el-icon {
  transform: translateX(5px);
}

.recommended-chefs {
  text-align: center;
  margin-bottom: 40px;
  color: #333;
}

.chef-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 25px;
  margin-top: 30px;
}

@media (max-width: 1200px) {
  .chef-grid {
    grid-template-columns: repeat(3, 1fr);
  }
}

@media (max-width: 900px) {
  .chef-grid {
    grid-template-columns: repeat(2, 1fr);
  }
}

@media (max-width: 600px) {
  .chef-grid {
    grid-template-columns: 1fr;
  }
}

.chef-card {
  background: white;
  border-radius: 16px;
  padding: 25px;
  text-align: center;
  transition: all 0.4s cubic-bezier(0.4, 0, 0.2, 1);
  cursor: pointer;
  border: 2px solid transparent;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.08);
  opacity: 0;
  animation: fadeInUp 0.6s ease-out forwards;
  position: relative;
  overflow: hidden;
}

.chef-card::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 0;
  background: linear-gradient(180deg, rgba(102, 126, 234, 0.05) 0%, rgba(118, 75, 162, 0.05) 100%);
  transition: height 0.4s ease;
  z-index: 0;
}

.chef-card:hover::before {
  height: 100%;
}

.chef-card:hover {
  transform: translateY(-8px);
  box-shadow: 0 12px 28px rgba(102, 126, 234, 0.2);
  border-color: rgba(102, 126, 234, 0.3);
}

.chef-card > * {
  position: relative;
  z-index: 1;
}

.chef-avatar {
  position: relative;
  margin-bottom: 20px;
  display: inline-block;
}

.chef-level-badge {
  position: absolute;
  bottom: -5px;
  right: -5px;
  padding: 4px 12px;
  border-radius: 12px;
  font-size: 12px;
  font-weight: 600;
  color: white;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.15);
}

.chef-level-badge.level-gold {
  background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%);
}

.chef-level-badge.level-silver {
  background: linear-gradient(135deg, #4facfe 0%, #00f2fe 100%);
}

.chef-level-badge.level-bronze {
  background: linear-gradient(135deg, #f6d365 0%, #fda085 100%);
}

.chef-name {
  font-size: 20px;
  color: #333;
  margin-bottom: 15px;
  font-weight: 600;
}

.chef-tags {
  display: flex;
  justify-content: center;
  gap: 8px;
  margin-bottom: 15px;
  flex-wrap: wrap;
}

.skill-tag {
  background: linear-gradient(135deg, #f5f7fa 0%, #c3cfe2 100%);
  padding: 5px 12px;
  border-radius: 15px;
  font-size: 13px;
  color: #555;
  font-weight: 500;
}

.chef-rating {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 6px;
  margin-bottom: 12px;
}

.star-icon {
  color: #ffd700;
  font-size: 18px;
}

.rating-value {
  font-size: 18px;
  font-weight: bold;
  color: #333;
}

.rating-count {
  font-size: 13px;
  color: #999;
}

.chef-service-count {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 6px;
  margin-bottom: 12px;
  font-size: 14px;
  color: #666;
}

.chef-service-count .el-icon {
  color: #667eea;
}

.chef-price {
  margin-bottom: 18px;
}

.price-label {
  display: block;
  font-size: 13px;
  color: #999;
  margin-bottom: 5px;
}

.price-value {
  font-size: 24px;
  font-weight: bold;
  color: #667eea;
}

.view-detail-btn {
  width: 100%;
  padding: 12px 0;
  font-size: 15px;
  font-weight: 500;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border: none;
  transition: all 0.3s ease;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
}

.view-detail-btn:hover {
  transform: translateX(3px);
  box-shadow: 0 6px 16px rgba(102, 126, 234, 0.4);
}

.view-detail-btn .el-icon {
  transition: transform 0.3s ease;
}

.view-detail-btn:hover .el-icon {
  transform: translateX(3px);
}

/* 滚动渐入动画 - 核心样式 */
.scroll-animate {
  opacity: 0;
  transition: opacity 0.6s ease-out, transform 0.6s ease-out;
  will-change: opacity, transform;
}

/* 从上到下淡入（用于 banner 等顶部元素） */
.scroll-animate[data-direction="top"] {
  transform: translateY(-80px);
}

/* 从下到上淡入（用于大部分内容区域） */
.scroll-animate[data-direction="bottom"] {
  transform: translateY(80px);
}

/* 激活状态 - 元素进入视口时 */
.scroll-animate.animate-active {
  opacity: 1;
  transform: translateY(0);
}

/* 子元素的延迟动画 */
.feature-item,
.chef-card {
  opacity: 0;
  animation: fadeInUp 0.6s ease-out forwards;
}

.scroll-animate.animate-active .feature-item,
.scroll-animate.animate-active .chef-card {
  /* 由父容器控制整体动画，子元素使用 delay */
}

@keyframes fadeInUp {
  from {
    opacity: 0;
    transform: translateY(20px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}
</style>
