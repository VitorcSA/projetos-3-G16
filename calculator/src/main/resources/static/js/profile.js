document.addEventListener('DOMContentLoaded', () => {
	fetch('/api/user/profile').then(response => {
		if (!response.ok) {
			window.location.replace('/login');
			return;
		}
		return response.json();
	}).then(data => {
		const form = document.getElementById('profile_form');

		if (data) {
			form.name.value = data.name;
			form.email.value = data.email;
			form.staff_count.value = data.staffCount;
			form.digital_staff_count.value = data.digitalCardStaffCount;

			if (data.address) {
				form.street.value = data.address.street;
				form.number.value = data.address.number;
				form.city.value = data.address.city;
				form.state.value = data.address.state;
				form.zip_code.value = data.address.zipCode;
			}
		}

		const addressFields = [
			form.street,
			form.number,
			form.city,
			form.state,
			form.zip_code
		];

		function validateAdressFields() {
			const anyFilled = addressFields.some(field => field.value.trim() !== "");

			addressFields.forEach(field => {
				field.required = anyFilled;
			});
		}


		addressFields.forEach(field => {
			field.addEventListener('input', validateAdressFields);
		});

		validateAdressFields();

		form.addEventListener("submit", event => {
			event.preventDefault();

			const payload = {
				name: form.name.value,
				email: form.email.value,
				password: form.password.value,
				staffCount: form.staff_count.value,
				address: {
					street: form.street.value,
					number: form.number.value,
					city: form.city.value,
					state: form.state.value,
					zipCode: form.zip_code.value
				},
				digitalCardStaffCount: form.digital_staff_count.value ? parseInt(form.digital_staff_count.value) : null
			};

			fetch('/api/user/profile', {
				method: `PUT`,
				headers: {
					'Content-Type': 'application/json'
				},
				body: JSON.stringify(payload)
			}).then(async response => {
				const text = await response.text();

				if (response.ok) {
					alert(text);
				} else {
					document.getElementById("error").innerText = text;
				}

			}).catch(error => {
				alert("Erro");
			});
		});

	}).catch(() => {
		window.location.replace('/login');
	});
});

async function searchByCep(input) {
	const form = input.form;
	const cep = input.value.replace('-', '');

	if (cep.length !== 8) return;

	const res = await fetch(`https://viacep.com.br/ws/${cep}/json/`);
	const data = await res.json();

	if (data.erro) return;

	form.querySelector('[name="street"]').value = data.logradouro;
	form.querySelector('[name="city"]').value = data.localidade;
	form.querySelector('[name="state"]').value = data.uf;
}
