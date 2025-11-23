// Login Form Handler
const loginForm = document.querySelector('.login-form');
if (loginForm) {
    loginForm.onsubmit = async (e) => {
        e.preventDefault();
        console.log('Login form submitted!');

        try {
            const res = await fetch('/api/auth/login', {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify({
                    email: loginForm.email.value,
                    password: loginForm.password.value
                })
            });

            if (res.ok) {
                const authResponse = await res.json();
                const jwtToken = authResponse.token;

                // Save the token to local storage
                localStorage.setItem('jwt_token', jwtToken);

                // Redirect to the notes page
                window.location.href = '/ui/notes';
            } else {
                const error = await res.json().catch(() => ({ message: 'Login failed' }));
                alert('Login failed: ' + (error.message || 'Invalid credentials'));
            }
        } catch (error) {
            console.error('Login error:', error);
            alert('An error occurred during login. Please try again.');
        }
    };
} else {
    console.log('Login form not found on this page.');
}

// Signup Form Handler
const signupForm = document.querySelector('.signup-form');
if (signupForm) {
    signupForm.onsubmit = async (e) => {
        e.preventDefault();
        console.log('Signup form submitted!');

        try {
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

                // Save the token to local storage
                localStorage.setItem('jwt_token', jwtToken);

                // Redirect to the notes page
                window.location.href = '/ui/notes';
            } else {
                const error = await res.json().catch(() => ({ message: 'Signup failed' }));
                alert('Signup failed: ' + (error.message || 'Unknown error'));
            }
        } catch (error) {
            console.error('Signup error:', error);
            alert('An error occurred during signup. Please try again.');
        }
    };
} else {
    console.log('Signup form not found on this page.');
}

// Notes Page - Load and display notes
if (window.location.pathname === '/ui/notes') {
    const jwtToken = localStorage.getItem('jwt_token');

    if (!jwtToken) {
        // Redirect to login if no token
        window.location.href = '/ui/login';
    } else {
        // Load notes when page loads
        loadNotes();
    }
}

async function loadNotes() {
    const jwtToken = localStorage.getItem('jwt_token');

    try {
        const res = await fetch('/api/notes', {
            method: 'GET',
            headers: {
                'Authorization': `Bearer ${jwtToken}`,
                'Content-Type': 'application/json'
            }
        });

        if (res.ok) {
            const notesData = await res.json();
            displayNotes(notesData);
        } else if (res.status === 401 || res.status === 403) {
            // Token expired or invalid
            localStorage.removeItem('jwt_token');
            window.location.href = '/ui/login';
        } else {
            console.error('Failed to load notes');
        }
    } catch (error) {
        console.error('Error loading notes:', error);
    }
}

function displayNotes(notesData) {
    const notesContainer = document.getElementById('notes-container');
    if (!notesContainer) return;

    notesContainer.innerHTML = '';

    if (notesData.content && notesData.content.length > 0) {
        notesData.content.forEach(note => {
            const noteDiv = document.createElement('div');
            noteDiv.className = 'note-item';
            noteDiv.innerHTML = `
                <h3>${escapeHtml(note.title)}</h3>
                <p>${escapeHtml(note.content || '')}</p>
                <p><strong>Tags:</strong> ${note.tags ? note.tags.join(', ') : 'None'}</p>
                <small>Updated: ${new Date(note.updatedAt).toLocaleString()}</small>
                <hr>
            `;
            notesContainer.appendChild(noteDiv);
        });
    } else {
        notesContainer.innerHTML = '<p>No notes found. Create your first note!</p>';
    }
}

function escapeHtml(text) {
    if (!text) return '';
    const div = document.createElement('div');
    div.textContent = text;
    return div.innerHTML;
}