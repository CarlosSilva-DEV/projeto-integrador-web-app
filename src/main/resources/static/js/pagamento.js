// Alternar entre métodos de pagamento
document.querySelectorAll('.method-tab').forEach(tab => {
	tab.addEventListener('click', function() {
		// Remover classe active de todas as tabs
		document.querySelectorAll('.method-tab').forEach(t => {
			t.classList.remove('active');
		});

		// Adicionar classe active à tab clicada
		this.classList.add('active');

		// Esconder todos os conteúdos
		document.querySelectorAll('.method-content').forEach(content => {
			content.classList.remove('active');
		});

		// Mostrar conteúdo correspondente
		const method = this.dataset.method;
		document.getElementById(`${method}-method`).classList.add('active');
	});
});

// Formatação do número do cartão
document.getElementById('cardNumber').addEventListener('input', function(e) {
	let value = e.target.value.replace(/\D/g, '');
	value = value.replace(/(\d{4})(?=\d)/g, '$1 ');
	e.target.value = value.substring(0, 19);

	// Atualizar preview do cartão
	const preview = document.querySelector('.card-number');
	if (value.length > 0) {
		const lastFour = value.replace(/\s/g, '').slice(-4);
		preview.textContent = `**** **** **** ${lastFour}`;
	} else {
		preview.textContent = '**** **** **** 1234';
	}
});

// Formatação da validade
document.getElementById('cardExpiry').addEventListener('input', function(e) {
	let value = e.target.value.replace(/\D/g, '');
	if (value.length >= 2) {
		value = value.substring(0, 2) + '/' + value.substring(2, 4);
	}
	e.target.value = value.substring(0, 5);

	// Atualizar preview
	const preview = document.querySelector('.card-expiry');
	preview.textContent = value || '12/25';
});

// Formatação do nome no cartão
document.getElementById('cardName').addEventListener('input', function(e) {
	const preview = document.querySelector('.card-name');
	preview.textContent = e.target.value.toUpperCase() || 'MARIA SILVA';
});

// Copiar código PIX
document.getElementById('copyPixBtn').addEventListener('click', function() {
	const pixCode = document.getElementById('pixCode');
	const textArea = document.createElement('textarea');
	textArea.value = pixCode.textContent;
	document.body.appendChild(textArea);
	textArea.select();
	document.execCommand('copy');
	document.body.removeChild(textArea);

	// Feedback visual
	const originalText = this.innerHTML;
	this.innerHTML = '<i class="fas fa-check"></i>';
	this.style.background = '#28a745';

	setTimeout(() => {
		this.innerHTML = originalText;
		this.style.background = '';
	}, 2000);
});

// Processar pagamento
document.getElementById('payNowBtn').addEventListener('click', function() {
	const activeMethod = document.querySelector('.method-tab.active').dataset.method;

	switch (activeMethod) {
		case 'pix':
			alert('Pagamento via PIX selecionado. O QR Code será gerado e o pagamento processado.');
			// Aqui seria integrado com a geração do QR Code PIX
			break;
		case 'credit':
			// Validar campos do cartão
			const cardNumber = document.getElementById('cardNumber').value;
			const cardName = document.getElementById('cardName').value;
			const cardExpiry = document.getElementById('cardExpiry').value;
			const cardCvv = document.getElementById('cardCvv').value;

			if (!cardNumber || !cardName || !cardExpiry || !cardCvv) {
				alert('Por favor, preencha todos os campos do cartão.');
				return;
			}

			alert('Pagamento via Cartão de Crédito processado com sucesso!');
			break;
		case 'boleto':
			alert('Boleto gerado com sucesso! Será enviado para seu e-mail.');
			break;
	}

	// Simular redirecionamento após pagamento
	setTimeout(() => {
		window.location.href = 'perfil.html';
	}, 2000);
});

// Inicializar máscaras e formatações
document.addEventListener('DOMContentLoaded', function() {
	// Máscara para CVV (apenas números)
	document.getElementById('cardCvv').addEventListener('input', function(e) {
		this.value = this.value.replace(/\D/g, '').substring(0, 3);
	});
});