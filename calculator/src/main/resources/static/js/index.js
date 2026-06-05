import {openProfileDialog} from './profile.js'

document.getElementById("profile_button").addEventListener("click", () => {
    openProfileDialog(document.getElementById("profile_dialog"));
});

fetch('/api/calculator/calculate', {
	method: 'POST',
	credentials: 'include'
}).then(async response => {
	if (!response.ok) return;

	const data = await response.json();
	const div = document.getElementById('calculation_info');

	showCalculation(data);
});

function showCalculation(data) {
    const difference = data.annual_physical_emission - data.annual_digital_emission;
 
    document.getElementById('physical_value').innerText = `${data.annual_physical_emission.toFixed(3)} kg`;
    document.getElementById('digital_value').innerText = `${data.annual_digital_emission.toFixed(3)} kg`;
    document.getElementById('saved_value').innerText = `${difference.toFixed(3)} kg`;
    document.getElementById('money_value').innerText = `$${data.money_wasted.toFixed(2)}`;
}
