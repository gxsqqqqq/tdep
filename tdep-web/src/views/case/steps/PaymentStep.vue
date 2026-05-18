<template>
  <div class="card" style="padding: 24px">
    <h3 style="margin-top: 0">诉讼缴费</h3>

    <el-alert
      title="缴费功能正在建设中"
      description="当前后端尚未提供缴费接口，此页面为预留界面。当缴费接口就绪后将自动对接。"
      type="info"
      show-icon
      style="margin-bottom: 24px"
    />

    <div style="padding: 24px; border: 1px solid var(--border-color); border-radius: 8px; text-align: center">
      <div style="font-size: 48px; margin-bottom: 16px">💰</div>
      <h4 style="margin: 0 0 8px">待缴费用</h4>
      <p style="color: var(--text-secondary); margin: 0 0 16px">
        您的案件已通过立案审核，请在规定时间内完成缴费。
      </p>
      <el-button type="primary" disabled>前往缴费（即将上线）</el-button>
    </div>
  </div>
</template>
