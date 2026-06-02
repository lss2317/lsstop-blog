<template>
  <v-dialog
    v-model="dialogVisible"
    :fullscreen="isMobile"
    max-width="400"
    transition="dialog-bottom-transition"
    @after-leave="onDialogClosed"
  >
    <v-card class="login-container">
      <v-icon class="close-btn" @click="closeAllDialogs"> mdi-close</v-icon>

      <transition :name="dialogVisible ? 'fade' : ''" mode="out-in">
        <!-- 密码登录 -->
        <div v-if="loginDialog" key="login" class="login-wrapper">
          <div class="input-group">
            <input
              v-model="loginForm.email"
              type="email"
              class="form-input"
              placeholder=" "
              autocomplete="email"
              maxlength="100"
              @keydown.space.prevent
              @paste="stripSpaces"
              @input="stripSpaces"
            />
            <label class="form-label">邮箱</label>
          </div>
          <div class="input-group">
            <input
              v-model="loginForm.password"
              :type="loginForm.showPwd ? 'text' : 'password'"
              class="form-input"
              placeholder=" "
              autocomplete="current-password"
              maxlength="20"
              @keydown.space.prevent
              @paste="stripSpaces"
              @input="stripSpaces"
            />
            <label class="form-label">密码</label>
            <span class="input-icon" @click="loginForm.showPwd = !loginForm.showPwd">
              <v-icon size="20">{{ loginForm.showPwd ? 'mdi-eye' : 'mdi-eye-off' }}</v-icon>
            </span>
          </div>
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
              >验证码登录</span
            >
            <span @click="openResetPasswordDialog" class="tip-link">忘记密码?</span>
          </div>
          <div class="social-login-title">其他登录方式</div>
          <div class="social-login-wrapper">
            <a @click="weiboLogin" class="mr-3 iconfont iconweibo" style="color: #e05244" />
            <a @click="qqLogin" class="iconfont iconqq" style="color: #00aaee" />
          </div>
        </div>

        <!-- 验证码登录 -->
        <div v-else-if="codeLoginDialog" key="codeLogin" class="login-wrapper">
          <div class="input-group">
            <input
              v-model="codeLoginForm.email"
              type="email"
              class="form-input"
              placeholder=" "
              autocomplete="email"
              maxlength="100"
              @keydown.space.prevent
              @paste="stripSpaces"
              @input="stripSpaces"
            />
            <label class="form-label">邮箱</label>
          </div>
          <div class="input-group">
            <input
              v-model="codeLoginForm.code"
              type="text"
              class="form-input"
              placeholder=" "
              autocomplete="one-time-code"
              maxlength="6"
              @keydown.space.prevent
              @paste="stripSpaces"
              @input="stripSpaces"
            />
            <label class="form-label">验证码</label>
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
            :loading="codeLoginLoading"
            :disabled="codeLoginLoading"
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
          <div class="input-group">
            <input
              v-model="registerForm.email"
              type="email"
              class="form-input"
              placeholder=" "
              autocomplete="email"
              maxlength="100"
              @keydown.space.prevent
              @paste="stripSpaces"
              @input="stripSpaces"
            />
            <label class="form-label">邮箱</label>
          </div>
          <div class="input-group">
            <input
              v-model="registerForm.code"
              type="text"
              class="form-input"
              placeholder=" "
              autocomplete="one-time-code"
              maxlength="6"
              @keydown.space.prevent
              @paste="stripSpaces"
              @input="stripSpaces"
            />
            <label class="form-label">验证码</label>
            <span
              class="send-code-btn"
              :class="{ disabled: registerForm.countdown > 0 }"
              @click="sendCodeForRegister"
            >
              {{ registerForm.countdown > 0 ? `${registerForm.countdown}s` : '发送' }}
            </span>
          </div>
          <div class="input-group">
            <input
              v-model="registerForm.password"
              :type="registerForm.showPwd ? 'text' : 'password'"
              class="form-input"
              placeholder=" "
              autocomplete="new-password"
              maxlength="20"
              @keydown.space.prevent
              @paste="stripSpaces"
              @input="stripSpaces"
            />
            <label class="form-label">密码</label>
            <span class="input-icon" @click="registerForm.showPwd = !registerForm.showPwd">
              <v-icon size="20">{{ registerForm.showPwd ? 'mdi-eye' : 'mdi-eye-off' }}</v-icon>
            </span>
          </div>
          <div class="input-group">
            <input
              v-model="registerForm.confirmPassword"
              :type="registerForm.showConfirmPwd ? 'text' : 'password'"
              class="form-input"
              placeholder=" "
              autocomplete="new-password"
              maxlength="20"
              @keydown.space.prevent
              @paste="stripSpaces"
              @input="stripSpaces"
            />
            <label class="form-label">确认密码</label>
            <span
              class="input-icon"
              @click="registerForm.showConfirmPwd = !registerForm.showConfirmPwd"
            >
              <v-icon size="20">{{
                registerForm.showConfirmPwd ? 'mdi-eye' : 'mdi-eye-off'
              }}</v-icon>
            </span>
          </div>
          <v-btn
            class="mt-10 login-btn"
            block
            color="#1976d2"
            rounded="lg"
            size="large"
            :loading="registerLoading"
            :disabled="registerLoading"
            @click="register"
          >
            注册
          </v-btn>
          <div class="mt-6 login-tip">
            <span class="tip-text">已有账号?</span>
            <span @click="openLoginDialog" class="tip-link ml-2">立即登录</span>
          </div>
        </div>

        <!-- 重置密码 -->
        <div v-else-if="resetPasswordDialog" key="resetPassword" class="login-wrapper">
          <div class="title">重置密码</div>
          <div class="input-group mt-6">
            <input
              v-model="resetPasswordForm.email"
              type="email"
              class="form-input"
              placeholder=" "
              autocomplete="email"
              maxlength="100"
              @keydown.space.prevent
              @paste="stripSpaces"
              @input="stripSpaces"
            />
            <label class="form-label">邮箱</label>
          </div>
          <div class="input-group">
            <input
              v-model="resetPasswordForm.code"
              type="text"
              class="form-input"
              placeholder=" "
              autocomplete="one-time-code"
              maxlength="6"
              @keydown.space.prevent
              @paste="stripSpaces"
              @input="stripSpaces"
            />
            <label class="form-label">验证码</label>
            <span
              class="send-code-btn"
              :class="{ disabled: resetPasswordForm.countdown > 0 }"
              @click="sendCodeForResetPassword"
            >
              {{ resetPasswordForm.countdown > 0 ? `${resetPasswordForm.countdown}s` : '发送' }}
            </span>
          </div>
          <div class="input-group">
            <input
              v-model="resetPasswordForm.password"
              :type="resetPasswordForm.showPwd ? 'text' : 'password'"
              class="form-input"
              placeholder=" "
              autocomplete="new-password"
              maxlength="20"
              @keydown.space.prevent
              @paste="stripSpaces"
              @input="stripSpaces"
            />
            <label class="form-label">新密码</label>
            <span
              class="input-icon"
              @click="resetPasswordForm.showPwd = !resetPasswordForm.showPwd"
            >
              <v-icon size="20">{{ resetPasswordForm.showPwd ? 'mdi-eye' : 'mdi-eye-off' }}</v-icon>
            </span>
          </div>
          <div class="input-group">
            <input
              v-model="resetPasswordForm.confirmPassword"
              :type="resetPasswordForm.showConfirmPwd ? 'text' : 'password'"
              class="form-input"
              placeholder=" "
              autocomplete="new-password"
              maxlength="20"
              @keydown.space.prevent
              @paste="stripSpaces"
              @input="stripSpaces"
            />
            <label class="form-label">确认密码</label>
            <span
              class="input-icon"
              @click="resetPasswordForm.showConfirmPwd = !resetPasswordForm.showConfirmPwd"
            >
              <v-icon size="20">{{
                resetPasswordForm.showConfirmPwd ? 'mdi-eye' : 'mdi-eye-off'
              }}</v-icon>
            </span>
          </div>
          <v-btn
            class="mt-10 login-btn"
            block
            color="#1976d2"
            rounded="lg"
            size="large"
            :loading="resetPasswordLoading"
            :disabled="resetPasswordLoading"
            @click="handleResetPassword"
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
import { reactive, computed, ref } from 'vue';
import { useLoginStore } from '@/stores/modules/login';
import { storeToRefs } from 'pinia';
import {
  emailLogin,
  emailCodeLogin,
  sendEmailCode,
  CodePurpose,
  resetPassword,
  register as registerApi,
} from '@/apis/auth';
import useUserInfoStore, { type UserInfo } from '@/stores/modules/userInfo';
import useLikeStore from '@/stores/modules/like';
import { useSnackbarStore } from '@/stores/modules/snackbar';
import { tokenManager } from '@/utils/token';
import { isValidEmail } from '@/utils/validate';
import { getErrorMessage } from '@/utils/error';
import { stripSpaces } from '@/utils/format';
import { OAUTH_CONFIG } from '@/constants/oauth';

const loginStore = useLoginStore();
const userInfoStore = useUserInfoStore();
const likeStore = useLikeStore();
const snackbar = useSnackbarStore();
const { dialogVisible, loginDialog, codeLoginDialog, registerDialog, resetPasswordDialog } =
  storeToRefs(loginStore);
const {
  closeAllDialogs,
  openLoginDialog,
  openCodeLoginDialog,
  openRegisterDialog,
  openResetPasswordDialog,
  onDialogClosed,
} = loginStore;

// 登录loading状态
const loginLoading = ref(false);

// 重置密码loading状态
const resetPasswordLoading = ref(false);

// 响应式判断
const isMobile = computed(() => {
  const clientWidth = document.documentElement.clientWidth;
  return clientWidth <= 960;
});

// 密码登录表单
const loginForm = reactive({
  email: '',
  password: '',
  showPwd: false,
});

// 验证码登录表单
const codeLoginForm = reactive({
  email: '',
  code: '',
  countdown: 0,
});

// 注册表单
const registerForm = reactive({
  email: '',
  code: '',
  password: '',
  confirmPassword: '',
  showPwd: false,
  showConfirmPwd: false,
  countdown: 0,
});

// 重置密码表单
const resetPasswordForm = reactive({
  email: '',
  code: '',
  password: '',
  confirmPassword: '',
  showPwd: false,
  showConfirmPwd: false,
  countdown: 0,
});

// 发送验证码通用函数
const startCountdown = (form: { countdown: number }) => {
  if (form.countdown > 0) return false;
  form.countdown = 60;
  const timer = setInterval(() => {
    form.countdown--;
    if (form.countdown <= 0) {
      clearInterval(timer);
    }
  }, 1000);
  return true;
};

// 发送登录验证码
const sendCodeForLogin = async () => {
  codeLoginForm.email = codeLoginForm.email.trim();
  if (!codeLoginForm.email) {
    snackbar.info('请输入邮箱');
    return;
  }
  if (!isValidEmail(codeLoginForm.email)) {
    snackbar.info('邮箱格式不正确');
    return;
  }
  if (codeLoginForm.countdown > 0) return;
  try {
    await sendEmailCode({ email: codeLoginForm.email, purpose: CodePurpose.LOGIN });
    snackbar.success('验证码已发送');
    startCountdown(codeLoginForm);
  } catch (error) {
    snackbar.error(getErrorMessage(error));
  }
};

// 发送注册验证码
const sendCodeForRegister = async () => {
  registerForm.email = registerForm.email.trim();
  if (!registerForm.email) {
    snackbar.info('请输入邮箱');
    return;
  }
  if (!isValidEmail(registerForm.email)) {
    snackbar.info('邮箱格式不正确');
    return;
  }
  if (registerForm.countdown > 0) return;
  try {
    await sendEmailCode({ email: registerForm.email, purpose: CodePurpose.REGISTER });
    snackbar.success('验证码已发送');
    startCountdown(registerForm);
  } catch (error) {
    snackbar.error(getErrorMessage(error));
  }
};

// 发送重置密码验证码
const sendCodeForResetPassword = async () => {
  resetPasswordForm.email = resetPasswordForm.email.trim();
  if (!resetPasswordForm.email) {
    snackbar.info('请输入邮箱');
    return;
  }
  if (!isValidEmail(resetPasswordForm.email)) {
    snackbar.info('邮箱格式不正确');
    return;
  }
  if (resetPasswordForm.countdown > 0) return;
  try {
    await sendEmailCode({ email: resetPasswordForm.email, purpose: CodePurpose.RESET_PASSWORD });
    snackbar.success('验证码已发送');
    startCountdown(resetPasswordForm);
  } catch (error) {
    snackbar.error(getErrorMessage(error));
  }
};

// 登录成功统一处理
const handleLoginSuccess = (data: UserInfo) => {
  userInfoStore.setUserInfo(data);
  if (data.accessToken && data.refreshToken) {
    tokenManager.setTokens(data.accessToken, data.refreshToken);
  }
  void likeStore.fetchUserLike();
  snackbar.success('登录成功');
  closeAllDialogs();
};

// 密码登录
const login = async () => {
  // 表单预处理
  loginForm.email = loginForm.email.trim();
  // 表单验证
  if (!loginForm.email) {
    snackbar.info('请输入邮箱');
    return;
  }
  if (!isValidEmail(loginForm.email)) {
    snackbar.info('邮箱格式不正确');
    return;
  }
  if (!loginForm.password) {
    snackbar.info('请输入密码');
    return;
  }

  loginLoading.value = true;
  try {
    const res = await emailLogin({
      email: loginForm.email,
      password: loginForm.password,
    });
    handleLoginSuccess(res.data);
    loginForm.email = '';
    loginForm.password = '';
  } catch (error) {
    snackbar.error(getErrorMessage(error));
  } finally {
    loginLoading.value = false;
  }
};

// 验证码登录loading状态
const codeLoginLoading = ref(false);

// 验证码登录
const codeLogin = async () => {
  codeLoginForm.email = codeLoginForm.email.trim();
  codeLoginForm.code = codeLoginForm.code.trim();
  if (!codeLoginForm.email) {
    snackbar.info('请输入邮箱');
    return;
  }
  if (!isValidEmail(codeLoginForm.email)) {
    snackbar.info('邮箱格式不正确');
    return;
  }
  if (!codeLoginForm.code) {
    snackbar.info('请输入验证码');
    return;
  }

  codeLoginLoading.value = true;
  try {
    const res = await emailCodeLogin({
      email: codeLoginForm.email,
      code: codeLoginForm.code,
    });
    handleLoginSuccess(res.data);
    codeLoginForm.email = '';
    codeLoginForm.code = '';
  } catch (error) {
    snackbar.error(getErrorMessage(error));
  } finally {
    codeLoginLoading.value = false;
  }
};

// 注册loading状态
const registerLoading = ref(false);

// 注册
const register = async () => {
  if (registerLoading.value) return;
  // 表单预处理
  registerForm.email = registerForm.email.trim();
  registerForm.code = registerForm.code.trim();
  // 表单验证
  if (!registerForm.email) {
    snackbar.info('请输入邮箱');
    return;
  }
  if (!isValidEmail(registerForm.email)) {
    snackbar.info('邮箱格式不正确');
    return;
  }
  if (!registerForm.code) {
    snackbar.info('请输入验证码');
    return;
  }
  const trimmedPassword = registerForm.password.trim();
  if (!trimmedPassword) {
    snackbar.info('请输入密码');
    return;
  }
  if (trimmedPassword.length < 6 || trimmedPassword.length > 20) {
    snackbar.info('密码长度为6-20位');
    return;
  }
  if (trimmedPassword !== registerForm.confirmPassword.trim()) {
    snackbar.info('两次密码输入不一致');
    return;
  }

  registerLoading.value = true;
  try {
    const res = await registerApi({
      email: registerForm.email,
      password: trimmedPassword,
      code: registerForm.code,
    });
    // 注册成功后自动登录
    handleLoginSuccess(res.data);
    // 重置表单
    registerForm.email = '';
    registerForm.code = '';
    registerForm.password = '';
    registerForm.confirmPassword = '';
  } catch (error) {
    snackbar.error(getErrorMessage(error));
  } finally {
    registerLoading.value = false;
  }
};

// 重置密码
const handleResetPassword = async () => {
  if (resetPasswordLoading.value) return;
  // 表单预处理
  resetPasswordForm.email = resetPasswordForm.email.trim();
  resetPasswordForm.code = resetPasswordForm.code.trim();
  if (!resetPasswordForm.email) {
    snackbar.info('请输入邮箱');
    return;
  }
  if (!isValidEmail(resetPasswordForm.email)) {
    snackbar.info('邮箱格式不正确');
    return;
  }
  if (!resetPasswordForm.code) {
    snackbar.info('请输入验证码');
    return;
  }
  const trimmedPassword = resetPasswordForm.password.trim();
  if (!trimmedPassword) {
    snackbar.info('请输入新密码');
    return;
  }
  if (trimmedPassword.length < 6 || trimmedPassword.length > 20) {
    snackbar.info('密码长度为6-20位');
    return;
  }
  if (trimmedPassword !== resetPasswordForm.confirmPassword.trim()) {
    snackbar.info('两次密码输入不一致');
    return;
  }

  resetPasswordLoading.value = true;
  try {
    await resetPassword({
      email: resetPasswordForm.email,
      code: resetPasswordForm.code,
      newPassword: trimmedPassword,
    });
    snackbar.success('密码重置成功');
    // 重置表单
    resetPasswordForm.email = '';
    resetPasswordForm.code = '';
    resetPasswordForm.password = '';
    resetPasswordForm.confirmPassword = '';
    // 跳转到登录
    openLoginDialog();
  } catch (error) {
    snackbar.error(getErrorMessage(error));
  } finally {
    resetPasswordLoading.value = false;
  }
};

// QQ登录
const qqLogin = () => {
  const { appId, redirectUri } = OAUTH_CONFIG.qq;
  const params = new URLSearchParams({
    response_type: 'code',
    client_id: appId,
    redirect_uri: redirectUri,
    scope: 'get_user_info',
  });
  window.location.href = `https://graph.qq.com/oauth2.0/authorize?${params.toString()}`;
};

// 微博登录
const weiboLogin = () => {
  const { appId, redirectUri } = OAUTH_CONFIG.weibo;
  const params = new URLSearchParams({
    client_id: appId,
    redirect_uri: redirectUri,
    response_type: 'code',
  });
  window.location.href = `https://api.weibo.com/oauth2/authorize?${params.toString()}`;
};
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

/* 自定义输入框样式 */
.input-group {
  position: relative;
  margin-top: 24px;
}

.input-group:first-child {
  margin-top: 0;
}

.form-input {
  width: 100%;
  padding: 12px 0;
  font-size: 16px;
  color: #333;
  border: none;
  border-bottom: 1px solid #ddd;
  outline: none;
  background: transparent;
  transition: border-color 0.3s ease;
}

.form-input:focus {
  border-bottom-color: #1976d2;
}

.form-input:focus ~ .form-label,
.form-input:not(:placeholder-shown) ~ .form-label {
  top: -8px;
  font-size: 12px;
  color: #1976d2;
}

.form-label {
  position: absolute;
  left: 0;
  top: 12px;
  font-size: 16px;
  color: #999;
  pointer-events: none;
  transition:
    top 0.3s ease,
    font-size 0.3s ease;
}

.input-icon {
  position: absolute;
  right: 0;
  top: 50%;
  transform: translateY(-50%);
  color: #999;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 4px;
}

.input-icon:hover {
  color: #666;
}

.send-code-btn {
  position: absolute;
  right: 0;
  top: 50%;
  transform: translateY(-50%);
  color: #1976d2;
  font-size: 14px;
  cursor: pointer;
  font-weight: 500;
}

.send-code-btn:hover:not(.disabled) {
  color: #1565c0;
}

.send-code-btn.disabled {
  color: #bbb;
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

/* 内部切换动画 */
.fade-enter-active,
.fade-leave-active {
  transition: opacity 0.15s ease;
}

.fade-enter-from,
.fade-leave-to {
  opacity: 0;
}
</style>

<!-- 夜间模式样式 -->
<style>
.v-theme--dark .login-container {
  background: #2a2a2a;
}

.v-theme--dark .title {
  color: var(--color-text-primary);
}

.v-theme--dark .form-input {
  color: var(--color-text-primary);
  border-bottom-color: var(--color-border);
}

.v-theme--dark .form-input:focus {
  border-bottom-color: #1976d2;
}

.v-theme--dark .form-label {
  color: var(--color-text-tertiary);
}

.v-theme--dark .tip-link,
.v-theme--dark .tip-text {
  color: var(--color-text-primary);
}

.v-theme--dark .tip-link:hover {
  color: #1976d2;
}

.v-theme--dark .input-icon {
  color: var(--color-text-tertiary);
}

.v-theme--dark .input-icon:hover {
  color: var(--color-text-primary);
}

.v-theme--dark .close-btn {
  color: var(--color-text-tertiary);
}

.v-theme--dark .close-btn:hover {
  color: var(--color-text-primary);
}

.v-theme--dark .social-login-title {
  color: var(--color-text-tertiary);
}

.v-theme--dark .social-login-title::before,
.v-theme--dark .social-login-title::after {
  background-color: var(--color-border);
}
</style>
