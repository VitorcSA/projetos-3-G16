import {loadStates,loadCities} from './address.js'

fetch('/api/user/profile').then(async response => {
	if (!response.ok) {
		const text = await response.text();
		console.log("Erro:", text);
		window.location.replace('/login');
		return null;
	}
	return response.json();
}).then(async data => {
	const form = document.getElementById('profile_form');
	const state = form.querySelector("#state");
	const city = form.querySelector("#city");
	
	await loadStates(state);	
	
	if (data) {
		form.name.value = data.name;
		form.email.value = data.email;
		form.staff_count.value = data.staff_count;
		form.digital_staff_count.value = data.digital_card_staff_count;

		if (data.address) {
			state.value = data.address.state;
			await loadCities(state, city);
			city.value = data.address.city;  
			city.required = true;
		}
	}
	
	state.addEventListener("change",async () => {
		await loadCities(state,city);
		city.required = state.value !== "";
	});
	
	loadStates(state);

	form.addEventListener("submit", event => {
		event.preventDefault();

		const payload = {
			name: form.name.value,
			email: form.email.value,
			password: form.password.value,
			staff_count: form.staff_count.value,
			address: state.value ? {
				city: city.value,
				state: state.value,
			} : null,
			digital_card_staff_count: form.digital_staff_count.value ? parseInt(form.digital_staff_count.value) : null
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