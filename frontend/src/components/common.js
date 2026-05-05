// 通用工具函数

// 切换密码可见性
function togglePasswordVisibility(inputId) {
    const passwordInput = document.getElementById(inputId);
    const toggleIcon = passwordInput.nextElementSibling.querySelector('i');
    
    if (passwordInput.type === 'password') {
        passwordInput.type = 'text';
        toggleIcon.classList.remove('fa-eye');
        toggleIcon.classList.add('fa-eye-slash');
    } else {
        passwordInput.type = 'password';
        toggleIcon.classList.remove('fa-eye-slash');
        toggleIcon.classList.add('fa-eye');
    }
}

// 验证码倒计时功能
function startCountdown(buttonId, seconds) {
    let countdown = seconds;
    let countdownInterval;
    const sendCodeBtn = document.getElementById(buttonId);

    // 禁用按钮并开始倒计时
    sendCodeBtn.disabled = true;
    sendCodeBtn.textContent = `${countdown}秒后重新发送`;

    countdownInterval = setInterval(() => {
        countdown--;
        sendCodeBtn.textContent = `${countdown}秒后重新发送`;

        if (countdown <= 0) {
            clearInterval(countdownInterval);
            sendCodeBtn.disabled = false;
            sendCodeBtn.textContent = '发送验证码';
            countdown = seconds; // 重置倒计时
        }
    }, 1000);
}

// 动态设置页面标题
function setPageTitle(title) {
    document.title = title;
}

// 动态设置登录/注册表单标题
function setFormTitle(title) {
    const titleElement = document.getElementById('login-title') || document.getElementById('register-title');
    if (titleElement) {
        titleElement.textContent = title;
    }
}

// 动态设置链接
function setLinkHref(linkId, href) {
    const linkElement = document.getElementById(linkId);
    if (linkElement) {
        linkElement.href = href;
    }
}

// 动态设置占位符
function setPlaceholder(inputId, placeholder) {
    const inputElement = document.getElementById(inputId);
    if (inputElement) {
        inputElement.placeholder = placeholder;
    }
}