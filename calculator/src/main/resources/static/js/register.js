import { loadStates, loadCities } from './address.js'

function nextStep() {
    const container = document.getElementById('register_container');
	const form = container.querySelector("#register_form");
    const email = form.querySelector("#email");
    const password = form.querySelector("#password");

    if (!email.reportValidity() || !password.reportValidity()) return;

    form.querySelector("#step1").style.display = 'none';
    form.querySelector("#step2").style.display = 'flex';

    container.querySelector("#register_title").innerText = 'Cadastre seus dados';

    container.querySelector('#login_text').style.display = 'none';
}

document.addEventListener('DOMContentLoaded', async () => {
    const form = document.getElementById("register_form");
    const state = form.querySelector("#state");
    const city = form.querySelector("#city");

	form.querySelector("#next_step_button").addEventListener('click', nextStep);
	
    await loadStates(state);

    state.addEventListener("change", async () => {
        await loadCities(state, city);
    });

    form.addEventListener('submit', function(event) {
        event.preventDefault();

        const formData = new FormData(this);
        const payload = {
            name: formData.get('name'),
            email: formData.get('email'),
            password: formData.get('password'),
            staff_count: parseInt(formData.get('staff_count')),
            address: state.value ? {
                city: city.value,
                state: state.value
            } : null,
            digitalCardStaffCount: formData.get('digital_staff_count')
                ? parseInt(formData.get('digital_staff_count'))
                : null
        };

        fetch('/api/auth/register', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(payload)
        }).then(async res => {
            const text = await res.text();
            if (res.ok) {
                alert(text);
                window.location.href = '/';
            } else {
                document.getElementById("error").innerText = text;
            }
        });
    });
});