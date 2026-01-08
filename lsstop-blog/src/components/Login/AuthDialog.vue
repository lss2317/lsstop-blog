<template>
  <v-dialog v-model="dialogVisible" :fullscreen="isMobile" max-width="400">
    <v-card class="login-container">
      <v-icon class="close-btn" @click="closeAllDialogs"> mdi-close </v-icon>

      <transition name="fade" mode="out-in">
        <!-- 密码登录 -->
        <div v-if="loginDialog" key="login" class="login-wrapper">
          <v-text-field v-model="loginForm.email" label="邮箱" variant="underlined" hide-details />
          <v-text-field
            v-model="loginForm.password"
            class="mt-8"
            label="密码"
            variant="underlined"
            hide-details
            :append-inner-icon="loginForm.showPwd ? 'mdi-eye' : 'mdi-eye-off'"
            :type="loginForm.showPwd ? 'text' : 'password'"
            @click:append-inner="loginForm.showPwd = !loginForm.showPwd"
          />
          <v-btn
            class="mt-10 login-btn"
            block
            color="#1976d2"
            rounded="lg"
            size="large"
            :loading="loginLoading"
            :disabled="loginLoading"
            @click="login"
          >
            登录
          </v-btn>
          <div class="mt-6 login-tip">
            <span @click="openRegisterDialog" class="tip-link">立即注册</span>
            <span
              @click="openCodeLoginDialog"
              class="tip-link"
              style="margin-left: auto; margin-right: 12px"
              >免密登录</span
            >
            <span @click="openForgetDialog" class="tip-link">忘记密码?</span>
          </div>
          <div class="social-login-title">其他登录方式</div>
          <div class="social-login-wrapper">
            <a @click="weiboLogin" class="mr-3 iconfont iconweibo" style="color: #e05244" />
            <a @click="qqLogin" class="iconfont iconqq" style="color: #00aaee" />
          </div>
        </div>

        <!-- 免密登录 -->
        <div v-else-if="codeLoginDialog" key="codeLogin" class="login-wrapper">
          <v-text-field
            v-model="codeLoginForm.email"
            label="邮箱"
            variant="underlined"
            hide-details
          />
          <div class="code-input-wrapper mt-8">
            <v-text-field
              v-model="codeLoginForm.code"
              label="验证码"
              variant="underlined"
              hide-details
            />
            <span
              class="send-code-btn"
              :class="{ disabled: codeLoginForm.countdown > 0 }"
              @click="sendCodeForLogin"
            >
              {{ codeLoginForm.countdown > 0 ? `${codeLoginForm.countdown}s` : '发送' }}
            </span>
          </div>
          <v-btn
            class="mt-10 login-btn"
            block
            color="#1976d2"
            rounded="lg"
            size="large"
            @click="codeLogin"
          >
            登录
          </v-btn>
          <div class="mt-6 login-tip center">
            <span @click="openLoginDialog" class="tip-link">返回密码登录</span>
          </div>
        </div>

        <!-- 注册 -->
        <div v-else-if="registerDialog" key="register" class="login-wrapper">
          <v-text-field
            v-model="registerForm.email"
            label="邮箱"
            variant="underlined"
            hide-details
          />
          <div class="code-input-wrapper mt-8">
            <v-text-field
              v-model="registerForm.code"
              label="验证码"
              variant="underlined"
              hide-details
            />
            <span
              class="send-code-btn"
              :class="{ disabled: registerForm.countdown > 0 }"
              @click="sendCodeForRegister"
            >
              {{ registerForm.countdown > 0 ? `${registerForm.countdown}s` : '发送' }}
            </span>
          </div>
          <v-text-field
            v-model="registerForm.password"
            class="mt-8"
            label="密码"
            variant="underlined"
            hide-details
            :append-inner-icon="registerForm.showPwd ? 'mdi-eye' : 'mdi-eye-off'"
            :type="registerForm.showPwd ? 'text' : 'password'"
            @click:append-inner="registerForm.showPwd = !registerForm.showPwd"
          />
          <v-text-field
            v-model="registerForm.confirmPassword"
            class="mt-8"
            label="确认密码"
            variant="underlined"
            hide-details
            :append-inner-icon="registerForm.showConfirmPwd ? 'mdi-eye' : 'mdi-eye-off'"
            :type="registerForm.showConfirmPwd ? 'text' : 'password'"
            @click:append-inner="registerForm.showConfirmPwd = !registerForm.showConfirmPwd"
          />
          <v-btn
            class="mt-10 login-btn"
            block
            color="#1976d2"
            rounded="lg"
            size="large"
            @click="register"
          >
            注册
          </v-btn>
          <div class="mt-6 login-tip">
            <span class="tip-text">已有账号?</span>
            <span @click="openLoginDialog" class="tip-link ml-2">立即登录</span>
          </div>
        </div>

        <!-- 忘记密码 -->
        <div v-else-if="forgetDialog" key="forget" class="login-wrapper">
          <div class="title">找回密码</div>
          <v-text-field
            v-model="forgetForm.email"
            class="mt-6"
            label="邮箱"
            variant="underlined"
            hide-details
          />
          <div class="code-input-wrapper mt-8">
            <v-text-field
              v-model="forgetForm.code"
              label="验证码"
              variant="underlined"
              hide-details
            />
            <span
              class="send-code-btn"
              :class="{ disabled: forgetForm.countdown > 0 }"
              @click="sendCodeForForget"
            >
              {{ forgetForm.countdown > 0 ? `${forgetForm.countdown}s` : '发送' }}
            </span>
          </div>
          <v-text-field
            v-model="forgetForm.password"
            class="mt-8"
            label="新密码"
            variant="underlined"
            hide-details
            :append-inner-icon="forgetForm.showPwd ? 'mdi-eye' : 'mdi-eye-off'"
            :type="forgetForm.showPwd ? 'text' : 'password'"
            @click:append-inner="forgetForm.showPwd = !forgetForm.showPwd"
          />
          <v-text-field
            v-model="forgetForm.confirmPassword"
            class="mt-8"
            label="确认密码"
            variant="underlined"
            hide-details
            :append-inner-icon="forgetForm.showConfirmPwd ? 'mdi-eye' : 'mdi-eye-off'"
            :type="forgetForm.showConfirmPwd ? 'text' : 'password'"
            @click:append-inner="forgetForm.showConfirmPwd = !forgetForm.showConfirmPwd"
          />
          <v-btn
            class="mt-10 login-btn"
            block
            color="#1976d2"
            rounded="lg"
            size="large"
            @click="resetPassword"
          >
            重置密码
          </v-btn>
          <div class="mt-6 login-tip center">
            <span @click="openLoginDialog" class="tip-link">返回登录</span>
          </div>
        </div>
      </transition>
    </v-card>
  </v-dialog>
</template>

<script setup lang="ts">
import { reactive, computed, ref } from 'vue'
import { useLoginStore } from '@/stores/modules/login'
import { storeToRefs } from 'pinia'
import { emailLogin } from '@/apis/auth'
import useUserInfoStore from '@/stores/modules/userInfo'
import useLikeStore from '@/stores/modules/like'
import { useSnackbarStore } from '@/stores/modules/snackbar'
import { tokenManager } from '@/utils/token'

const loginStore = useLoginStore()
const userInfoStore = useUserInfoStore()
const likeStore = useLikeStore()
const snackbar = useSnackbarStore()
const { dialogVisible, loginDialog, codeLoginDialog, registerDialog, forgetDialog } =
  storeToRefs(loginStore)
const {
  closeAllDialogs,
  openLoginDialog,
  openCodeLoginDialog,
  openRegisterDialog,
  openForgetDialog,
} = loginStore

// 登录loading状态
const loginLoading = ref(false)

// 响应式判断
const isMobile = computed(() => {
  const clientWidth = document.documentElement.clientWidth
  return clientWidth <= 960
})

// 密码登录表单
const loginForm = reactive({
  email: '',
  password: '',
  showPwd: false,
})

// 免密登录表单
const codeLoginForm = reactive({
  email: '',
  code: '',
  countdown: 0,
})

// 注册表单
const registerForm = reactive({
  email: '',
  code: '',
  password: '',
  confirmPassword: '',
  showPwd: false,
  showConfirmPwd: false,
  countdown: 0,
})

// 忘记密码表单
const forgetForm = reactive({
  email: '',
  code: '',
  password: '',
  confirmPassword: '',
  showPwd: false,
  showConfirmPwd: false,
  countdown: 0,
})

// 发送验证码通用函数
const startCountdown = (form: { countdown: number }) => {
  if (form.countdown > 0) return false
  form.countdown = 60
  const timer = setInterval(() => {
    form.countdown--
    if (form.countdown <= 0) {
      clearInterval(timer)
    }
  }, 1000)
  return true
}

// 发送登录验证码
const sendCodeForLogin = () => {
  if (startCountdown(codeLoginForm)) {
    // TODO: 实现发送验证码逻辑
  }
}

// 发送注册验证码
const sendCodeForRegister = () => {
  if (startCountdown(registerForm)) {
    // TODO: 实现发送验证码逻辑
  }
}

// 发送忘记密码验证码
const sendCodeForForget = () => {
  if (startCountdown(forgetForm)) {
    // TODO: 实现发送验证码逻辑
  }
}

// 密码登录
const login = async () => {
  // 表单验证
  if (!loginForm.email) {
    snackbar.info('请输入邮箱')
    return
  }
  if (!loginForm.password) {
    snackbar.info('请输入密码')
    return
  }

  loginLoading.value = true
  try {
    const res = await emailLogin({
      email: loginForm.email,
      password: loginForm.password,
    })
    // 判断业务状态码
    if (res.code !== 200) {
      snackbar.error(res.msg || '登录失败')
      return
    }
    // 保存用户信息到store
    userInfoStore.setUserInfo(res.data)
    // 保存token
    if (res.data.accessToken && res.data.refreshToken) {
      tokenManager.setTokens(res.data.accessToken, res.data.refreshToken)
    }
    // 获取用户点赞数据
    void likeStore.fetchUserLike()
    snackbar.success('登录成功')
    // 关闭弹框并重置表单
    closeAllDialogs()
    loginForm.email = ''
    loginForm.password = ''
  } catch (error: unknown) {
    const err = error as { response?: { data?: { msg?: string } } }
    const msg = err.response?.data?.msg || '网络错误，请稍后重试'
    snackbar.error(msg)
  } finally {
    loginLoading.value = false
  }
}

// 验证码登录
const codeLogin = () => {
  // TODO: 实现验证码登录逻辑
}

// 注册
const register = () => {
  // TODO: 实现注册逻辑
}

// 重置密码
const resetPassword = () => {
  // TODO: 实现重置密码逻辑
}

// 微博登录
const weiboLogin = () => {
  // TODO: 实现微博登录
}

// QQ登录
const qqLogin = () => {
  // TODO: 实现QQ登录
}
</script>

<style scoped>
.login-container {
  border-radius: 12px !important;
  padding: 0;
  position: relative;
}

.close-btn {
  position: absolute;
  top: 16px;
  right: 16px;
  color: #999;
  cursor: pointer;
  z-index: 1;
}

.close-btn:hover {
  color: #666;
}

.login-wrapper {
  padding: 60px 50px 40px;
}

.title {
  font-size: 20px;
  font-weight: 500;
  color: #333;
  text-align: center;
}

.login-btn {
  text-transform: none;
  font-size: 16px;
  letter-spacing: 0;
}

.login-tip {
  display: flex;
  align-items: center;
}

.login-tip.center {
  justify-content: center;
}

.tip-link {
  color: #333;
  font-size: 14px;
  cursor: pointer;
}

.tip-link:hover {
  color: #1976d2;
}

.tip-text {
  color: #333;
  font-size: 14px;
  cursor: default;
}

.code-input-wrapper {
  position: relative;
  display: flex;
  align-items: flex-end;
}

.code-input-wrapper .v-text-field {
  flex: 1;
}

.send-code-btn {
  position: absolute;
  right: 0;
  bottom: 8px;
  color: #999;
  font-size: 14px;
  cursor: pointer;
}

.send-code-btn:hover:not(.disabled) {
  color: #1976d2;
}

.send-code-btn.disabled {
  color: #ccc;
  cursor: not-allowed;
}

.social-login-title {
  margin-top: 2rem;
  color: #b5b5b5;
  font-size: 0.8rem;
  text-align: center;
}

.social-login-title::before {
  content: '';
  display: inline-block;
  background-color: #e0e0e0;
  width: 80px;
  height: 1px;
  margin: 0 12px;
  vertical-align: middle;
}

.social-login-title::after {
  content: '';
  display: inline-block;
  background-color: #e0e0e0;
  width: 80px;
  height: 1px;
  margin: 0 12px;
  vertical-align: middle;
}

.social-login-wrapper {
  margin-top: 1.2rem;
  text-align: center;
  display: flex;
  justify-content: center;
  gap: 16px;
}

.social-login-wrapper a {
  text-decoration: none;
  cursor: pointer;
  font-size: 2rem;
}

.social-login-wrapper a:hover {
  opacity: 0.8;
}

/* 切换动画 */
.fade-enter-active,
.fade-leave-active {
  transition: opacity 0.15s ease;
}

.fade-enter-from,
.fade-leave-to {
  opacity: 0;
}
</style>
