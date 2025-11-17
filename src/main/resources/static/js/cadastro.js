document.getElementById('signupForm').addEventListener('submit', async function(e) {
    e.preventDefault();
    clearErrorMessages();

    const fullName = document.getElementById('fullName').value.trim();
    const userLogin = document.getElementById('userLogin').value.trim();
    const email = document.getElementById('email').value.trim();
    const phone = document.getElementById('phone').value.trim();
    const password = document.getElementById('password').value;
    const confirmPassword = document.getElementById('confirmPassword').value;

    let isValid = true;

    // Validações
    if (fullName === '') {
        showError('fullNameError', 'Por favor, informe seu nome');
        isValid = false;
    }
	
	if (userLogin === '') {
	        showError('userLoginError', 'Por favor, informe seu login');
	        isValid = false;
	    }

    if (email === '') {
        showError('emailError', 'Por favor, informe seu e-mail');
        isValid = false;
    } else if (!isValidEmail(email)) {
        showError('emailError', 'Por favor, informe um e-mail válido');
        isValid = false;
    }
	
    if (phone === '') {
        showError('phoneError', 'Por favor, informe seu telefone');
        isValid = false;
    } else if (!isValidPhone(phone)) {
        showError('phoneError', 'Formato inválido. Use: (XX) XXXXX-XXXX');
        isValid = false;
    }

    if (password === '') {
        showError('passwordError', 'Por favor, crie uma senha');
        isValid = false;
    } else if (password.length < 8) {
        showError('passwordError', 'A senha deve ter pelo menos 8 caracteres');
        isValid = false;
    }

    if (confirmPassword === '') {
        showError('confirmPasswordError', 'Por favor, confirme sua senha');
        isValid = false;
    } else if (password !== confirmPassword) {
        showError('confirmPasswordError', 'As senhas não coincidem');
        isValid = false;
    }

    if (isValid) {
        const submitBtn = this.querySelector('.btn-primary');
        const originalText = submitBtn.textContent;

        submitBtn.textContent = 'Criando conta...';
        submitBtn.disabled = true;

        try {
            // Prepara os dados no formato esperado pelo backend
            const userData = {
                nome: fullName,
                login: userLogin,
                email: email,
                telefone: phone,
                senha: password
            };

            await authSystem.register(userData);
            
            alert('Cadastro realizado com sucesso! Faça login para continuar.');
            window.location.href = 'login.html';

        } catch (error) {
            submitBtn.textContent = originalText;
            submitBtn.disabled = false;
            
            if (error.message.includes('Login já está em uso')) {
                showError('fullNameError', 'Já existe um usuário com nome similar. Tente usar um nome diferente.');
            }
			
			if (error.message.includes('Email já está em uso')) {
                showError('emailError', 'Este e-mail já está cadastrado');
            }
        }
    }
});


function isValidPhone(phone) {
    // Regex para validar o formato (XX) XXXXX-XXXX
    const phoneRegex = /^\(\d{2}\) \d{5}-\d{4}$/;
    return phoneRegex.test(phone);
}

// FUNÇÃO DE FORMATAÇÃO DO TELEFONE NO INPUT
document.getElementById('phone').addEventListener('input', function(e) {
    let value = e.target.value.replace(/\D/g, '');
    
    // Limita a 11 dígitos
    if (value.length > 11) {
        value = value.substring(0, 11);
    }
    
    // Aplica a formatação (XX) XXXXX-XXXX
    if (value.length <= 2) {
        value = value.replace(/^(\d{0,2})/, '($1');
    } else if (value.length <= 7) {
        value = value.replace(/^(\d{2})(\d{0,5})/, '($1) $2');
    } else {
        value = value.replace(/^(\d{2})(\d{5})(\d{0,4})/, '($1) $2-$3');
    }
    
    e.target.value = value;
    
    // Validação visual em tempo real
    validatePhoneVisual(this);
});

// VALIDAÇÃO VISUAL EM TEMPO REAL
function validatePhoneVisual(input) {
    const phone = input.value;
    
    if (phone === '') {
        input.style.borderColor = '';
        return;
    }
    
    if (isValidPhone(phone)) {
        input.style.borderColor = '#28a745'; // Verde para válido
    } else {
        input.style.borderColor = '#dc3545'; // Vermelho para inválido
    }
}

// VALIDAÇÃO AO PERDER O FOCO (blur)
document.getElementById('phone').addEventListener('blur', function() {
    const phone = this.value;
    
    if (phone && !isValidPhone(phone)) {
        showError('phoneError', 'Formato inválido. Use: (XX) XXXXX-XXXX');
    } else {
        document.getElementById('phoneError').style.display = 'none';
    }
});


// Funções auxiliares (mantidas)
function isValidEmail(email) {
    const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
    return emailRegex.test(email);
}

function checkPasswordStrength(password) {
    let strength = 0;
    if (password.length >= 8) strength++;
    if (password.match(/([a-z].*[A-Z])|([A-Z].*[a-z])/)) strength++;
    if (password.match(/([0-9])/)) strength++;
    if (password.match(/([!,%,&,@,#,$,^,*,?,_,~])/)) strength++;
    return strength;
}

// Event listeners para validação em tempo real
document.getElementById('password').addEventListener('input', function() {
    const password = this.value;
    const strength = checkPasswordStrength(password);
    const strengthBar = document.querySelector('.password-strength-bar');

    strengthBar.className = 'password-strength-bar';
    
    if (password === '') return;

    if (strength <= 1) {
        strengthBar.classList.add('weak');
    } else if (strength <= 2) {
        strengthBar.classList.add('medium');
    } else {
        strengthBar.classList.add('strong');
    }
});

document.getElementById('confirmPassword').addEventListener('input', function() {
    const password = document.getElementById('password').value;
    const confirmPassword = this.value;

    if (confirmPassword && password !== confirmPassword) {
        this.style.borderColor = '#ff4d4d';
        showError('confirmPasswordError', 'As senhas não coincidem');
    } else {
        this.style.borderColor = '';
        document.getElementById('confirmPasswordError').style.display = 'none';
    }
});

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