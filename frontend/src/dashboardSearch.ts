export function initDashboardSearch(): void {
    const aiInput: HTMLInputElement = document.getElementById('aiPromptInput') as HTMLInputElement;
    if (!aiInput) return;
    let debounceTimer: number;
    aiInput.addEventListener('input', () => {
        clearTimeout(debounceTimer);
        debounceTimer = window.setTimeout(async () => onInputChanged(aiInput), 1500);
    });
}

async function onInputChanged(aiInput: HTMLInputElement) {
    const query = aiInput.value.trim();
    try {
        const url = new URL('http://localhost:8080/calculators');
        const resp = await fetch(url.toString(), {
            method: 'GET',
            credentials: 'include',
            body: query
        });
        if (!resp.ok) {
            console.error('Search failed:', resp.status);
            return;
        }
        const cards: Array<{ title: string; description: string }> = await resp.json();
        const container = document.getElementById('cardsContainer');
        if (!container) return;
        container.innerHTML = '';
        for (const card of cards) {
            const cardEl = document.createElement('section');
            cardEl.className = 'dashboard-card';
            const heading = document.createElement('h2');
            heading.textContent = card.title;
            const desc = document.createElement('p');
            desc.textContent = card.description;
            const btn = document.createElement('button');
            btn.className = 'btn btn-primary';
            btn.textContent = 'Calculate';
            btn.addEventListener('click', () => alert(`Clicked ${card.title}`));
            cardEl.append(heading, desc, btn);
            container.append(cardEl);
        }
    } catch (err) {
        console.error('Error during search:', err);
    }
}