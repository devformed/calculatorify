import {renderDashboardCards} from "./dashboard.js";
import {renderHistory} from "./history.js";
import {initSliders} from "./sliders.js";
import {initNavToggle} from "./nav.js";
import {initDashboardSearch} from "./dashboardSearch.js";

document.addEventListener('DOMContentLoaded', () => {
    initSliders();
    initNavToggle();
    renderHistory();
    renderDashboardCards();
    initDashboardSearch();
    // AI submit button navigates to modify view with prompt
    const aiInput = document.getElementById('aiPromptInput') as HTMLInputElement;
    const aiSubmit = document.getElementById('aiSubmit');
    if (aiInput && aiSubmit) {
        aiSubmit.addEventListener('click', () => {
            const prompt = aiInput.value.trim();
            if (!prompt) return;
            window.location.href = `modify.html?prompt=${encodeURIComponent(prompt)}`;
        });
    }
});