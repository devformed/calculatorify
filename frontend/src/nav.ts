/**
 * Initializes navigation menu toggle for smaller screens.
 */
export function initNavToggle(): void {
  const navToggle = document.getElementById("navToggle");
  const navMenu = document.getElementById("navMenu");
  if (navToggle && navMenu) {
    navToggle.addEventListener("click", () => {
      navMenu.classList.toggle("hide");
    });
  }
}