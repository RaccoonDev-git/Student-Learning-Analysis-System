/**
 * AI智能助手组件
 * 提供AI驱动的学习分析和建议功能
 */
class AIAssistant {
    constructor() {
        this.apiBaseUrl = 'http://localhost:8082/api/ai';
        this.currentModel = null;
        this.isLoading = false;
        this.init();
    }
    
    /**
     * 初始化AI助手
     */
    init() {
        this.loadAvailableModels();
        this.setupEventListeners();
    }
    
    /**
     * 设置事件监听器
     */
    setupEventListeners() {
        // AI分析按钮
        document.addEventListener('click', (e) => {
            if (e.target.matches('.ai-analyze-btn')) {
                e.preventDefault();
                const analysisType = e.target.dataset.analysisType || 'comprehensive';
                this.analyzeStudentLearning(null, analysisType); // 传入null，让方法自动获取studentId
            }
            
            if (e.target.matches('.ai-advice-btn')) {
                e.preventDefault();
                const context = e.target.dataset.context || '';
                this.generateStudyAdvice(null, context); // 传入null，让方法自动获取studentId
            }
            
            if (e.target.matches('.ai-chat-btn')) {
                e.preventDefault();
                this.openChatModal();
            }
        });
    }
    
    /**
     * 分析学生学习情况
     */
    async analyzeStudentLearning(studentId, analysisType) {
        if (this.isLoading) return;
        
        // 如果没有提供studentId，从token中获取
        if (!studentId) {
            studentId = this.getCurrentStudentId();
            if (!studentId) {
                this.showError('无法获取当前学生信息，请重新登录');
                return;
            }
        }
        
        this.showLoading('正在分析学习情况...');
        
        try {
            const token = localStorage.getItem('token');
            const response = await fetch(`${this.apiBaseUrl}/analyze/student/${studentId}?analysisType=${analysisType}`, {
                method: 'POST',
                headers: {
                    'Authorization': `Bearer ${token}`,
                    'Content-Type': 'application/json'
                }
            });
            
            const result = await response.json();
            
            if (result.success) {
                this.showAnalysisResult(result.content, '学习分析报告');
            } else {
                this.showError('分析失败: ' + result.error);
            }
            
        } catch (error) {
            console.error('AI分析失败:', error);
            this.showError('网络错误，请稍后重试');
        } finally {
            this.hideLoading();
        }
    }
    

    /**
     * 生成学习建议
     */
    async generateStudyAdvice(studentId, context) {
        if (this.isLoading) return;
        
        // 如果没有提供studentId，从token中获取
        if (!studentId) {
            studentId = this.getCurrentStudentId();
            if (!studentId) {
                this.showError('无法获取当前学生信息，请重新登录');
                return;
            }
        }
        
        this.showLoading('正在生成学习建议...');
        
        try {
            const token = localStorage.getItem('token');
            const response = await fetch(`${this.apiBaseUrl}/advice/study/${studentId}`, {
                method: 'POST',
                headers: {
                    'Authorization': `Bearer ${token}`,
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify({ context })
            });
            
            const result = await response.json();
            
            if (result.success) {
                this.showAnalysisResult(result.content, '学习建议');
            } else {
                this.showError('生成建议失败: ' + result.error);
            }
            
        } catch (error) {
            console.error('AI建议生成失败:', error);
            this.showError('网络错误，请稍后重试');
        } finally {
            this.hideLoading();
        }
    }
    
    /**
     * 打开AI对话模态框
     */
    openChatModal() {
        this.createChatModal();
    }
    
    /**
     * 创建AI对话模态框
     */
    createChatModal() {
        // 移除已存在的模态框
        const existingModal = document.getElementById('ai-chat-modal');
        if (existingModal) {
            existingModal.remove();
        }
        
        const modal = document.createElement('div');
        modal.id = 'ai-chat-modal';
        modal.className = 'ai-chat-modal';
        modal.innerHTML = `
            <div class="ai-chat-content">
                <div class="ai-chat-header">
                    <h3>AI智能助手</h3>
                    <button class="ai-chat-close">&times;</button>
                </div>
                <div class="ai-chat-messages" id="ai-chat-messages">
                    <div class="ai-message">
                        <div class="ai-avatar">🤖</div>
                        <div class="ai-text">你好！我是AI学习助手，有什么可以帮助你的吗？</div>
                    </div>
                </div>
                <div class="ai-chat-input">
                    <textarea id="ai-chat-input" placeholder="请输入你的问题..."></textarea>
                    <button id="ai-chat-send">发送</button>
                </div>
            </div>
        `;
        
        document.body.appendChild(modal);
        
        // 设置样式
        this.setupChatModalStyles();
        
        // 绑定事件
        this.setupChatModalEvents();
        
        // 显示模态框
        setTimeout(() => modal.classList.add('show'), 100);
    }
    
    /**
     * 设置对话模态框样式
     */
    setupChatModalStyles() {
        const style = document.createElement('style');
        style.textContent = `
            .ai-chat-modal {
                position: fixed;
                top: 0;
                left: 0;
                width: 100%;
                height: 100%;
                background: rgba(0, 0, 0, 0.5);
                display: flex;
                align-items: center;
                justify-content: center;
                z-index: 10000;
                opacity: 0;
                transition: opacity 0.3s ease;
            }
            
            .ai-chat-modal.show {
                opacity: 1;
            }
            
            .ai-chat-content {
                background: white;
                border-radius: 12px;
                width: 600px;
                height: 500px;
                display: flex;
                flex-direction: column;
                box-shadow: 0 10px 30px rgba(0, 0, 0, 0.3);
                color: #333;
            }
            
            .ai-chat-header {
                padding: 20px;
                border-bottom: 1px solid #eee;
                display: flex;
                justify-content: space-between;
                align-items: center;
            }
            
            .ai-chat-header h3 {
                margin: 0;
                color: #333;
            }
            
            .ai-chat-close {
                background: none;
                border: none;
                font-size: 24px;
                cursor: pointer;
                color: #666;
            }
            
            .ai-chat-messages {
                flex: 1;
                padding: 20px;
                overflow-y: auto;
                display: flex;
                flex-direction: column;
                gap: 15px;
                color: #333;
            }
            
            .ai-message {
                display: flex;
                gap: 10px;
                align-items: flex-start;
            }
            
            .user-message {
                flex-direction: row-reverse;
            }
            
            .ai-avatar, .user-avatar {
                width: 40px;
                height: 40px;
                border-radius: 50%;
                display: flex;
                align-items: center;
                justify-content: center;
                font-size: 18px;
                flex-shrink: 0;
            }
            
            .ai-avatar {
                background: #e3f2fd;
            }
            
            .user-avatar {
                background: #f3e5f5;
            }
            
            .ai-text, .user-text {
                max-width: 70%;
                padding: 12px 16px;
                border-radius: 18px;
                word-wrap: break-word;
            }
            
            .ai-text {
                background: #f5f5f5;
                color: #333 !important;
            }
            
            .user-text {
                background: #007bff;
                color: white;
            }
            
            .ai-chat-input {
                padding: 20px;
                border-top: 1px solid #eee;
                display: flex;
                gap: 10px;
            }
            
            .ai-chat-input textarea {
                flex: 1;
                padding: 12px;
                border: 1px solid #ddd;
                border-radius: 8px;
                resize: none;
                font-family: inherit;
            }
            
            .ai-chat-input button {
                padding: 12px 24px;
                background: #007bff;
                color: white;
                border: none;
                border-radius: 8px;
                cursor: pointer;
            }
            
            .ai-chat-input button:hover {
                background: #0056b3;
            }
            
            .ai-chat-input button:disabled {
                background: #ccc;
                cursor: not-allowed;
            }
        `;
        
        document.head.appendChild(style);
    }
    
    /**
     * 设置对话模态框事件
     */
    setupChatModalEvents() {
        const modal = document.getElementById('ai-chat-modal');
        const closeBtn = modal.querySelector('.ai-chat-close');
        const sendBtn = document.getElementById('ai-chat-send');
        const input = document.getElementById('ai-chat-input');
        
        // 关闭模态框
        closeBtn.addEventListener('click', () => {
            modal.classList.remove('show');
            setTimeout(() => modal.remove(), 300);
        });
        
        // 点击背景关闭
        modal.addEventListener('click', (e) => {
            if (e.target === modal) {
                closeBtn.click();
            }
        });
        
        // 发送消息
        const sendMessage = async () => {
            const message = input.value.trim();
            if (!message) return;
            
            // 添加用户消息
            this.addChatMessage(message, 'user');
            input.value = '';
            
            // 发送到AI
            await this.sendChatMessage(message);
        };
        
        sendBtn.addEventListener('click', sendMessage);
        
        // Enter键发送
        input.addEventListener('keydown', (e) => {
            if (e.key === 'Enter' && !e.shiftKey) {
                e.preventDefault();
                sendMessage();
            }
        });
    }
    
    /**
     * 添加聊天消息
     */
    addChatMessage(content, type) {
        const messagesContainer = document.getElementById('ai-chat-messages');
        const messageDiv = document.createElement('div');
        messageDiv.className = `${type}-message`;
        
        const avatar = type === 'user' ? '👤' : '🤖';
        const textClass = type === 'user' ? 'user-text' : 'ai-text';
        const avatarClass = type === 'user' ? 'user-avatar' : 'ai-avatar';
        
        messageDiv.innerHTML = `
            <div class="${avatarClass}">${avatar}</div>
            <div class="${textClass}">${content}</div>
        `;
        
        messagesContainer.appendChild(messageDiv);
        messagesContainer.scrollTop = messagesContainer.scrollHeight;
    }
    
    /**
     * 发送聊天消息到AI
     */
    async sendChatMessage(message) {
        try {
            const token = localStorage.getItem('token');
            const userId = JSON.parse(atob(token.split('.')[1])).userId;
            
            const response = await fetch(`${this.apiBaseUrl}/chat`, {
                method: 'POST',
                headers: {
                    'Authorization': `Bearer ${token}`,
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify({
                    message: message,
                    userId: userId.toString(),
                    context: 'learning'
                })
            });
            
            const result = await response.json();
            
            if (result.success) {
                this.addChatMessage(result.content, 'ai');
            } else {
                this.addChatMessage('抱歉，我暂时无法回答这个问题。请稍后再试。', 'ai');
            }
            
        } catch (error) {
            console.error('AI对话失败:', error);
            this.addChatMessage('网络错误，请检查网络连接后重试。', 'ai');
        }
    }
    
    /**
     * 显示分析结果
     */
    showAnalysisResult(content, title) {
        this.createResultModal(content, title);
    }
    
    /**
     * 创建结果模态框
     */
    createResultModal(content, title) {
        // 移除已存在的模态框
        const existingModal = document.getElementById('ai-result-modal');
        if (existingModal) {
            existingModal.remove();
        }
        
        const modal = document.createElement('div');
        modal.id = 'ai-result-modal';
        modal.className = 'ai-result-modal';
        modal.innerHTML = `
            <div class="ai-result-content">
                <div class="ai-result-header">
                    <h3>${title}</h3>
                    <button class="ai-result-close">&times;</button>
                </div>
                <div class="ai-result-body">
                    <div class="ai-result-text">${this.formatAIResponse(content)}</div>
                </div>
                <div class="ai-result-footer">
                    <button class="ai-result-copy">复制内容</button>
                    <button class="ai-result-close-btn">关闭</button>
                </div>
            </div>
        `;
        
        document.body.appendChild(modal);
        
        // 设置样式
        this.setupResultModalStyles();
        
        // 绑定事件
        this.setupResultModalEvents();
        
        // 显示模态框
        setTimeout(() => modal.classList.add('show'), 100);
    }
    
    /**
     * 格式化AI响应内容
     */
    formatAIResponse(content) {
        // 将换行符转换为HTML换行
        return content.replace(/\n/g, '<br>');
    }
    
    /**
     * 设置结果模态框样式
     */
    setupResultModalStyles() {
        const style = document.createElement('style');
        style.textContent = `
            .ai-result-modal {
                position: fixed;
                top: 0;
                left: 0;
                width: 100%;
                height: 100%;
                background: rgba(0, 0, 0, 0.5);
                display: flex;
                align-items: center;
                justify-content: center;
                z-index: 10000;
                opacity: 0;
                transition: opacity 0.3s ease;
            }
            
            .ai-result-modal.show {
                opacity: 1;
            }
            
            .ai-result-content {
                background: white;
                border-radius: 12px;
                width: 80%;
                max-width: 800px;
                max-height: 80%;
                display: flex;
                flex-direction: column;
                box-shadow: 0 10px 30px rgba(0, 0, 0, 0.3);
            }
            
            .ai-result-header {
                padding: 20px;
                border-bottom: 1px solid #eee;
                display: flex;
                justify-content: space-between;
                align-items: center;
            }
            
            .ai-result-header h3 {
                margin: 0;
                color: #333;
            }
            
            .ai-result-close {
                background: none;
                border: none;
                font-size: 24px;
                cursor: pointer;
                color: #666;
            }
            
            .ai-result-body {
                flex: 1;
                padding: 20px;
                overflow-y: auto;
            }
            
            .ai-result-text {
                line-height: 1.6;
                color: #333 !important;
            }
            
            .ai-result-footer {
                padding: 20px;
                border-top: 1px solid #eee;
                display: flex;
                gap: 10px;
                justify-content: flex-end;
            }
            
            .ai-result-footer button {
                padding: 10px 20px;
                border: none;
                border-radius: 6px;
                cursor: pointer;
            }
            
            .ai-result-copy {
                background: #28a745;
                color: white;
            }
            
            .ai-result-close-btn {
                background: #6c757d;
                color: white;
            }
            
            .ai-result-copy:hover {
                background: #218838;
            }
            
            .ai-result-close-btn:hover {
                background: #545b62;
            }
        `;
        
        document.head.appendChild(style);
    }
    
    /**
     * 设置结果模态框事件
     */
    setupResultModalEvents() {
        const modal = document.getElementById('ai-result-modal');
        const closeBtn = modal.querySelector('.ai-result-close');
        const closeBtn2 = modal.querySelector('.ai-result-close-btn');
        const copyBtn = modal.querySelector('.ai-result-copy');
        const textContent = modal.querySelector('.ai-result-text');
        
        // 关闭模态框
        const closeModal = () => {
            modal.classList.remove('show');
            setTimeout(() => modal.remove(), 300);
        };
        
        closeBtn.addEventListener('click', closeModal);
        closeBtn2.addEventListener('click', closeModal);
        
        // 点击背景关闭
        modal.addEventListener('click', (e) => {
            if (e.target === modal) {
                closeModal();
            }
        });
        
        // 复制内容
        copyBtn.addEventListener('click', () => {
            const text = textContent.textContent;
            navigator.clipboard.writeText(text).then(() => {
                copyBtn.textContent = '已复制';
                setTimeout(() => {
                    copyBtn.textContent = '复制内容';
                }, 2000);
            });
        });
    }
    
    /**
     * 显示加载状态
     */
    showLoading(message) {
        this.isLoading = true;
        
        // 移除已存在的加载提示
        const existingLoading = document.getElementById('ai-loading');
        if (existingLoading) {
            existingLoading.remove();
        }
        
        const loading = document.createElement('div');
        loading.id = 'ai-loading';
        loading.className = 'ai-loading';
        loading.innerHTML = `
            <div class="ai-loading-content">
                <div class="ai-loading-spinner"></div>
                <div class="ai-loading-text">${message}</div>
            </div>
        `;
        
        document.body.appendChild(loading);
        
        // 设置样式
        this.setupLoadingStyles();
        
        // 显示加载提示
        setTimeout(() => loading.classList.add('show'), 100);
    }
    
    /**
     * 隐藏加载状态
     */
    hideLoading() {
        this.isLoading = false;
        const loading = document.getElementById('ai-loading');
        if (loading) {
            loading.classList.remove('show');
            setTimeout(() => loading.remove(), 300);
        }
    }
    
    /**
     * 设置加载样式
     */
    setupLoadingStyles() {
        const style = document.createElement('style');
        style.textContent = `
            .ai-loading {
                position: fixed;
                top: 0;
                left: 0;
                width: 100%;
                height: 100%;
                background: rgba(0, 0, 0, 0.5);
                display: flex;
                align-items: center;
                justify-content: center;
                z-index: 10001;
                opacity: 0;
                transition: opacity 0.3s ease;
            }
            
            .ai-loading.show {
                opacity: 1;
            }
            
            .ai-loading-content {
                background: white;
                padding: 30px;
                border-radius: 12px;
                text-align: center;
                box-shadow: 0 10px 30px rgba(0, 0, 0, 0.3);
            }
            
            .ai-loading-spinner {
                width: 40px;
                height: 40px;
                border: 4px solid #f3f3f3;
                border-top: 4px solid #007bff;
                border-radius: 50%;
                animation: ai-spin 1s linear infinite;
                margin: 0 auto 15px;
            }
            
            @keyframes ai-spin {
                0% { transform: rotate(0deg); }
                100% { transform: rotate(360deg); }
            }
            
            .ai-loading-text {
                color: #333;
                font-size: 16px;
            }
        `;
        
        document.head.appendChild(style);
    }
    
    /**
     * 显示错误信息
     */
    showError(message) {
        alert('AI服务错误: ' + message);
    }
    
    /**
     * 获取当前登录用户的学生ID
     */
    getCurrentStudentId() {
        try {
            const token = localStorage.getItem('token');
            if (!token) return null;
            
            const payload = JSON.parse(atob(token.split('.')[1]));
            return payload.userId || payload.sub;
        } catch (error) {
            console.error('解析token失败:', error);
            return null;
        }
    }
    
    /**
     * 加载可用模型
     */
    async loadAvailableModels() {
        try {
            const response = await fetch(`${this.apiBaseUrl}/models`);
            const result = await response.json();
            
            if (result.available) {
                console.log('可用AI模型:', result.models);
                this.currentModel = result.models[0]; // 使用第一个可用模型
            }
        } catch (error) {
            console.error('加载AI模型失败:', error);
        }
    }
}

// 初始化AI助手
document.addEventListener('DOMContentLoaded', () => {
    window.aiAssistant = new AIAssistant();
});
