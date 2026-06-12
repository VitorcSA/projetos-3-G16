import { openProfileDialog } from './profile.js'

document.getElementById("profile_button").addEventListener("click", () => {
	openProfileDialog(document.getElementById("profile_dialog"));
});

fetch('/api/calculator/calculate', {
	method: 'POST',
	credentials: 'include'
}).then(async response => {
	if (!response.ok) return;

	const data = await response.json();

	showCalculation(data);
});

function showCalculation(data) {
	const difference = data.annual_physical_emission - data.annual_digital_emission;

	const format = (num) => num.toLocaleString('pt-BR', {
		minimumFractionDigits: 0,
		maximumFractionDigits: 1
	});

	document.getElementById('physical_value').innerText = `${format(data.annual_physical_emission)} kg`;
	document.getElementById('digital_value').innerText = `${format(data.annual_digital_emission)} kg`;
	document.getElementById('saved_value').innerText = `${format(difference)} kg`;
	document.getElementById('money_value').innerText = `$${format(data.money_wasted)}`;
}
