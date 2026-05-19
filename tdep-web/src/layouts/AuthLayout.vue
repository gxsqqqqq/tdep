<template>
  <div class="auth-layout">
    <div class="brand-section">
      <div class="brand-content">
        <div class="brand-logo">
          <span class="logo-icon">⚖️</span>
          <h1 class="logo-text">TDEP</h1>
        </div>

        <h2 class="brand-title">可信数字证据平台</h2>
        <p class="brand-subtitle">Trusted Digital Evidence Platform</p>

        <div class="features">
          <div class="feature-item">
            <div class="feature-icon">
              <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                <path d="M12 22s8-4 8-10V5l-8-3-8 3v7c0 6 8 10 8 10z"/>
              </svg>
            </div>
            <div class="feature-text">
              <h4>安全可信</h4>
              <p>区块链存证，不可篡改</p>
            </div>
          </div>

          <div class="feature-item">
            <div class="feature-icon">
              <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                <circle cx="12" cy="12" r="10"/>
                <polyline points="12,6 12,12 16,14"/>
              </svg>
            </div>
            <div class="feature-text">
              <h4>高效便捷</h4>
              <p>全流程在线办理</p>
            </div>
          </div>

          <div class="feature-item">
            <div class="feature-icon">
              <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                <path d="M17 21v-2a4 4 0 00-4-4H5a4 4 0 00-4 4v2"/>
                <circle cx="9" cy="7" r="4"/>
                <path d="M23 21v-2a4 4 0 00-3-3.87M16 3.13a4 4 0 010 7.75"/>
              </svg>
            </div>
            <div class="feature-text">
              <h4>多方协同</h4>
              <p>当事人、法官、书记员联动</p>
            </div>
          </div>
        </div>

        <div class="stats">
          <div class="stat-item">
            <span class="stat-number">10,000+</span>
            <span class="stat-label">案件处理</span>
          </div>
          <div class="stat-divider"></div>
          <div class="stat-item">
            <span class="stat-number">99.9%</span>
            <span class="stat-label">系统可用性</span>
          </div>
          <div class="stat-divider"></div>
          <div class="stat-item">
            <span class="stat-number">50+</span>
            <span class="stat-label">合作法院</span>
          </div>
        </div>
      </div>

      <div class="background-pattern"></div>
    </div>

    <div class="form-section">
      <div class="form-container">
        <router-view v-slot="{ Component, route }">
          <transition name="auth-fade" mode="out-in">
            <component :is="Component" :key="route.path" />
          </transition>
        </router-view>
      </div>

      <div class="footer-text">
        <p>&copy; {{ currentYear }} TDEP 可信数字证据平台 · 版权所有</p>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { computed } from 'vue'

const currentYear = computed(() => new Date().getFullYear())
</script>

<style scoped>
.auth-layout {
  display: flex;
  min-height: 100vh;
  background-color: #f8fafc;
}

.brand-section {
  position: relative;
  width: 45%;
  min-width: 480px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 60px 48px;
  overflow: hidden;
}

.brand-content {
  position: relative;
  z-index: 2;
  max-width: 420px;
  color: white;
}

.brand-logo {
  display: flex;
  align-items: center;
  gap: 14px;
  margin-bottom: 40px;
}

.logo-icon {
  font-size: 42px;
}

.logo-text {
  font-size: 32px;
  font-weight: 700;
  letter-spacing: -0.5px;
  margin: 0;
}

.brand-title {
  font-size: 28px;
  font-weight: 700;
  line-height: 1.3;
  margin: 0 0 12px 0;
}

.brand-subtitle {
  font-size: 15px;
  opacity: 0.85;
  margin: 0 0 48px 0;
  font-weight: 300;
}

.features {
  display: flex;
  flex-direction: column;
  gap: 24px;
  margin-bottom: 48px;
}

.feature-item {
  display: flex;
  align-items: flex-start;
  gap: 16px;
}

.feature-icon {
  width: 44px;
  height: 44px;
  border-radius: 12px;
  background: rgba(255, 255, 255, 0.15);
  backdrop-filter: blur(10px);
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
}

.feature-icon svg {
  width: 22px;
  height: 22px;
}

.feature-text h4 {
  font-size: 15px;
  font-weight: 600;
  margin: 0 0 4px 0;
}

.feature-text p {
  font-size: 13px;
  opacity: 0.8;
  margin: 0;
  line-height: 1.4;
}

.stats {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding-top: 32px;
  border-top: 1px solid rgba(255, 255, 255, 0.2);
}

.stat-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 6px;
}

.stat-number {
  font-size: 24px;
  font-weight: 700;
}

.stat-label {
  font-size: 12px;
  opacity: 0.75;
}

.stat-divider {
  width: 1px;
  height: 40px;
  background: rgba(255, 255, 255, 0.2);
}

.background-pattern {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  opacity: 0.08;
  background-image:
    radial-gradient(circle at 20% 30%, white 1px, transparent 1px),
    radial-gradient(circle at 80% 70%, white 1px, transparent 1px),
    radial-gradient(circle at 50% 90%, white 1px, transparent 1px);
  background-size: 60px 60px, 80px 80px, 100px 100px;
}

.form-section {
  flex: 1;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 40px;
  position: relative;
}

.form-container {
  width: 100%;
  max-width: 420px;
  background: white;
  border-radius: 20px;
  box-shadow:
    0 4px 6px -1px rgba(0, 0, 0, 0.05),
    0 10px 15px -3px rgba(0, 0, 0, 0.08);
  padding: 48px 40px;
}

.footer-text {
  margin-top: 32px;
  text-align: center;
}

.footer-text p {
  font-size: 13px;
  color: #94a3b8;
  margin: 0;
}

.auth-fade-enter-active,
.auth-fade-leave-active {
  transition: all 0.3s ease;
}

.auth-fade-enter-from {
  opacity: 0;
  transform: translateY(10px);
}

.auth-fade-leave-to {
  opacity: 0;
  transform: translateY(-10px);
}

@media (max-width: 1024px) {
  .brand-section {
    display: none;
  }

  .form-section {
    min-height: 100vh;
    padding: 24px;
  }

  .form-container {
    padding: 36px 28px;
    border-radius: 16px;
  }
}

@media (max-width: 480px) {
  .form-container {
    padding: 28px 20px;
    border-radius: 12px;
    box-shadow: none;
    background: transparent;
  }
}
</style>
