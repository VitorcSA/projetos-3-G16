document.documentElement.style.display = 'none';

fetch('/api/auth/validate').then(response => {
	if(!response.ok){
		window.location.replace('/login');
	}else{
		document.documentElement.style.display = 'block';
	}
}).catch(() => {
	window.location.replace('/login');
});