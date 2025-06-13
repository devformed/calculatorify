import { renderDashboardCards } from "./dashboard.js";
import { renderHistory } from "./history.js";
import { initSliders } from "./sliders.js";
import { initNavToggle } from "./nav.js";
import { initDashboardSearch } from "./dashboardSearch.js";

// Initialize dashboard page (script is loaded after DOM)
initSliders();
initNavToggle();
renderHistory();
renderDashboardCards();
initDashboardSearch();