// Simple SPA navigation and page rendering

document.addEventListener('DOMContentLoaded', () => {
    const main = document.getElementById('main-content');
    document.getElementById('nav-home').addEventListener('click', renderHome);
    document.getElementById('nav-add').addEventListener('click', renderAddDrink);
    renderHome(); // Default page
});

function renderHome() {
    const main = document.getElementById('main-content');
    main.innerHTML = `
        <h1>Drinks</h1>
        <div id="drinks-list">
            <p>Loading drinks...</p>
        </div>
    `;
    fetch('/api/drinks')
        .then(res => res.json())
        .then(drinks => {
            const list = document.getElementById('drinks-list');
            if (!drinks.length) {
                list.innerHTML = `<p>No drinks yet. Click 'Add Drink' to create one!</p>`;
                return;
            }
            list.innerHTML = drinks.map(drink => `
                <div class="drink-card">
                    ${drink.photo ? `<img src="/uploads/${drink.photo}" alt="${drink.name}" class="drink-photo">` : ''}
                    <h2>${drink.name}</h2>
                    <p><b>Ingredients:</b> ${drink.ingredients.join(', ')}</p>
                    <p><b>Price:</b> $${drink.price.toFixed(2)}</p>
                    <p><b>Location:</b> ${drink.location || '-'}</p>
                    <p><b>Made at home:</b> ${drink.homemade ? 'Yes' : 'No'}</p>
                    <button onclick="editDrink(${drink.id})">Edit</button>
                    <button onclick="deleteDrink(${drink.id})">Delete</button>
                </div>
            `).join('');
        });
}

