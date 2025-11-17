document.getElementById('loginForm').addEventListener('submit', async function(e) {
    e.preventDefault();
    clearErrorMessages();

    const login = document.getElementById('login').value.trim(); // Mudado de 'email' para 'login'
    const password = document.getElementById('password').value;
    const rememberMe = document.getElementById('remember').checked;

    let isValid = true;

    if (login === '') {
        showError('loginError', 'Por favor, informe seu login');
        isValid = false;
    }

    if (password === '') {
        showError('passwordError', 'Por favor, informe sua senha');
        isValid = false;
    }

    if (isValid) {
        const submitBtn = document.querySelector('.btn-primary');
        const originalText = submitBtn.textContent;

        submitBtn.textContent = 'Entrando...';
        submitBtn.disabled = true;

        try {
            // Usa o authSystem para fazer login
            await authSystem.login(login, password);
            
            // Salva o estado de "Lembrar-me" se necessário
            if (rememberMe) {
                localStorage.setItem('rememberMe', 'true');
                localStorage.setItem('userLogin', login); // Salva o login em vez do email
            } else {
                localStorage.removeItem('rememberMe');
                localStorage.removeItem('userLogin');
            }

        } catch (error) {
            submitBtn.textContent = originalText;
            submitBtn.disabled = false;
            
            // Mostra mensagem de erro adequada
            if (error.message.includes('Usuário não encontrado')) {
                showError('loginError', 'Login não encontrado');
            } else if (error.message.includes('Senha incorreta')) {
                showError('passwordError', 'Senha incorreta');
            } else {
                showError('loginError', 'Erro ao fazer login. Tente novamente.');
            }
        }
    }
});

// Preenche o login se "Lembrar-me" estava marcado
document.addEventListener('DOMContentLoaded', function() {
    const rememberMe = localStorage.getItem('rememberMe') === 'true';
    const savedLogin = localStorage.getItem('userLogin'); // Mudado de 'userEmail' para 'userLogin'

    if (rememberMe && savedLogin) {
        document.getElementById('login').value = savedLogin;
        document.getElementById('remember').checked = true;
    }
});

// Funções auxiliares
function showError(elementId, message) {
    const errorElement = document.getElementById(elementId);
    errorElement.textContent = message;
    errorElement.style.display = 'block';
}

function clearErrorMessages() {
    const errorElements = document.querySelectorAll('.error-message');
    errorElements.forEach(element => {
        element.textContent = '';
        element.style.display = 'none';
    });
}

// Efeitos visuais nos campos
const formInputs = document.querySelectorAll('.form-control');
formInputs.forEach(input => {
    input.addEventListener('focus', function() {
        this.style.borderColor = 'var(--primary)';
    });

    input.addEventListener('blur', function() {
        if (!this.value) {
            this.style.borderColor = '';
        }
    });
});

// Botão voltar para home
document.getElementById('backHomeBtn').addEventListener('click', function(e) {
    e.preventDefault();

    if (document.referrer && document.referrer.includes(window.location.hostname)) {
        window.history.back();
    } else {
        window.location.href = 'index.html';
    }
});