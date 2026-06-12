import { loadStates, loadCities } from './address.js';

const navbar = document.getElementById('navigation_bar');
const links = navbar.querySelectorAll('[data-path]');

links.forEach(link => {

	if (link.dataset.path === window.location.pathname) {
		link.classList.add('active');
	}

});

const buttons = navbar.querySelectorAll('[data-modal]');

buttons.forEach(button => {
	button.addEventListener('click', () => {
		const modal = document.getElementById(button.dataset.modal);
		modal.showModal();
	});
});

const form = document.getElementById('profile-form');
const state = document.getElementById('profile-state');
const city = document.getElementById('profile-city');

const currentState = state.dataset.selected;
const currentCity = city.dataset.selected;

await loadStates(state);

if (currentState) {
	state.value = currentState;
	await loadCities(state, city);
	city.value = currentCity;
	city.required = true;
}

state.addEventListener('change', async () => {
	await loadCities(state, city);
	city.required = state.value !== '';
});

form.addEventListener('submit', event => {
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
		digital_card_staff_count: form.digital_staff_count.value
			? parseInt(form.digital_staff_count.value)
			: null
	};

	fetch('/api/user/profile', {
		method: 'PUT',
		headers: { 'Content-Type': 'application/json' },
		body: JSON.stringify(payload)
	}).then(async response => {
		const text = await response.text();
		if (response.ok) {
			alert(text);
		} else {
			document.getElementById('profile-error').innerText = text;
		}
	}).catch(() => {
		alert('Erro');
	});
});
