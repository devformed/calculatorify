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
});