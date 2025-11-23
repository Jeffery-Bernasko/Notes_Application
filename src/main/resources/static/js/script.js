 const loginForm = document.querySelector('.login-form');
 if (loginForm) {
     loginForm.onsubmit = async (e) => {
         // Prevent default HTML form submission (the refresh/URL query)
         console.log('Login form submitted!');
         e.preventDefault();
         console.log('Login form submitted!');
         const res = await fetch('/api/auth/login', {
             method: 'POST',
             headers: { 'Content-Type': 'application/json' },
             body: JSON.stringify({
                 email: loginForm.email.value,
                 password: loginForm.password.value
             })
         });

         if (res.ok) {
             // 1. Get the AuthResponse body
             const authResponse = await res.json();
             const jwtToken = authResponse.token;

             // 2. Save the token to local storage for persistent authentication
             localStorage.setItem('jwt_token', jwtToken);

             // 3. Redirect to the notes page
             window.location.href = '/ui/notes';
         } else {
             alert('Login failed!'); // Handle the login error
         }
     };
 } else {
     console.error('Login form element not found. Check selector.');
 }