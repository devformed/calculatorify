/**
 * Initializes navigation menu toggle for smaller screens.
 */
// Global fetch wrapper: redirect to login.html on 401 Unauthorized
{
  const originalFetch = window.fetch.bind(window);
  window.fetch = async (...args) => {
    const response = await originalFetch(...args);
    if (response.status === 401) {
      window.location.href = 'login.html';
    }
    return response;
  };
}
export function initNavToggle(): void {
  const navToggle = document.getElementById("navToggle");
  const navMenu = document.getElementById("navMenu");
  if (navToggle && navMenu) {
    navToggle.addEventListener("click", () => {
      navMenu.classList.toggle("hide");
    });
  }
  // Attach logout handler to any "Log out" link in nav
  const logoutSelectors = [".nav-links a", "#navMenu a"];
  logoutSelectors.forEach(selector => {
    document.querySelectorAll<HTMLAnchorElement>(selector).forEach(link => {
      if (link.textContent?.trim() === 'Log out') {
        link.addEventListener('click', async event => {
          event.preventDefault();
          try {
            const resp = await fetch('http://localhost:8080/logout', {
              method: 'POST',
              credentials: 'include'
            });
            if (resp.ok) {
              window.location.href = 'login.html';
            } else {
              const msg = await resp.text();
              alert(`Logout failed: ${msg}`);
            }
          } catch (err) {
            console.error('Logout error:', err);
            alert('An error occurred during logout.');
          }
        });
      }
    });
  });
}