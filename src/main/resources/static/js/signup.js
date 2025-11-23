const signupForm = document.querySelector('.signup-form');
if (signupForm) {
    signupForm.onsubmit = async (e) => {
        e.preventDefault();
        console.log('Signup form submitted!');

        const res = await fetch('/api/auth/signup', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({
                email: signupForm.email.value,
                password: signupForm.password.value
            })
        });

        if (res.ok) {
            const authResponse = await res.json();
            const jwtToken = authResponse.token;
            localStorage.setItem('jwt_token', jwtToken);
            window.location.href = '/ui/notes';
        } else {
            const error = await res.json();
            alert('Signup failed: ' + (error.message || 'Unknown error'));
        }
    };
}