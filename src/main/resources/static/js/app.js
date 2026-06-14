document.addEventListener('DOMContentLoaded', () => {
    const navLinks = document.querySelectorAll('.nav-links a');
    const views = document.querySelectorAll('.view');
    const generateForm = document.getElementById('generate-form');
    const loadingState = document.getElementById('loading-state');
    const resultContainer = document.getElementById('result-container');
    const historyLoading = document.getElementById('history-loading');
    const historyGrid = document.getElementById('history-grid');
    const programmingLanguageSelect = document.getElementById('programmingLanguage');
    const frameworkSelect = document.getElementById('framework');

    const API_BASE_URL = '/api/projects';

    const frameworksByLanguage = {
        'Java': ['Spring Boot', 'Quarkus', 'Micronaut', 'Jakarta EE'],
        'Python': ['Django', 'Flask', 'FastAPI', 'Pyramid'],
        'C#': ['.NET Core', 'ASP.NET', 'Blazor'],
        'JavaScript': ['React', 'Angular', 'Vue.js', 'Express.js', 'Next.js', 'NestJS'],
        'TypeScript': ['Angular', 'Next.js', 'NestJS', 'Express.js', 'React'],
        'Go': ['Gin', 'Echo', 'Fiber', 'Standard Library']
    };

    // Update frameworks dropdown when language changes
    let lastLanguage = '';
    
    function updateFrameworks() {
        const lang = programmingLanguageSelect.value;
        // Do not rebuild the dropdown if the language hasn't actually changed!
        if (!lang || lang === lastLanguage) return;
        
        lastLanguage = lang;
        const frameworks = frameworksByLanguage[lang] || [];
        frameworkSelect.innerHTML = '<option value="" disabled selected>Select Framework</option>';
        frameworks.forEach(fw => {
            const option = document.createElement('option');
            option.value = fw;
            option.textContent = fw;
            frameworkSelect.appendChild(option);
        });
    }

    programmingLanguageSelect.addEventListener('change', updateFrameworks);
    
    // Fallbacks in case the browser autofills the language without firing a change event
    frameworkSelect.addEventListener('focus', updateFrameworks);
    frameworkSelect.addEventListener('click', updateFrameworks);
    
    // Call on load in case browser autofilled the form and the value is already available
    if (programmingLanguageSelect.value) {
        updateFrameworks();
    }

    // Navigation Logic
    navLinks.forEach(link => {
        link.addEventListener('click', (e) => {
            e.preventDefault();
            const targetPage = e.target.getAttribute('data-page');
            
            // Update active link
            navLinks.forEach(l => l.classList.remove('active'));
            e.target.classList.add('active');
            
            // Show target view
            views.forEach(view => {
                view.classList.remove('active');
                if (view.id === `${targetPage}-view`) {
                    view.classList.add('active');
                }
            });

            if (targetPage === 'history') {
                loadHistory();
            }
        });
    });

    // Form Submission
    generateForm.addEventListener('submit', async (e) => {
        e.preventDefault();
        
        const requestData = {
            skillLevel: document.getElementById('skillLevel').value,
            programmingLanguage: document.getElementById('programmingLanguage').value,
            framework: document.getElementById('framework').value,
            projectDomain: document.getElementById('projectDomain').value
        };

        // UI State
        generateForm.parentElement.classList.add('hidden');
        resultContainer.classList.add('hidden');
        loadingState.classList.remove('hidden');

        try {
            const response = await fetch(`${API_BASE_URL}/generate`, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify(requestData)
            });

            if (!response.ok) {
                throw new Error('Failed to generate project idea');
            }

            const data = await response.json();
            renderProjectDetails(data, resultContainer);
            
            loadingState.classList.add('hidden');
            resultContainer.classList.remove('hidden');
            
        } catch (error) {
            console.error(error);
            alert('An error occurred while generating the idea. Please check the backend connection and API keys.');
            
            // Reset UI State
            loadingState.classList.add('hidden');
            generateForm.parentElement.classList.remove('hidden');
        }
    });

    // Load History
    async function loadHistory() {
        historyGrid.innerHTML = '';
        historyLoading.classList.remove('hidden');

        try {
            const response = await fetch(`${API_BASE_URL}/history`);
            if (!response.ok) throw new Error('Failed to fetch history');

            const projects = await response.json();
            historyLoading.classList.add('hidden');

            if (projects.length === 0) {
                historyGrid.innerHTML = '<p style="color: var(--text-muted); text-align: center; grid-column: 1/-1;">No projects generated yet. Head over to the Generate tab!</p>';
                return;
            }

            projects.forEach(project => {
                const card = createHistoryCard(project);
                historyGrid.appendChild(card);
            });
            
        } catch (error) {
            console.error(error);
            historyLoading.innerHTML = '<p style="color: #ef4444;">Failed to load history.</p>';
        }
    }

    // Render Details
    function renderProjectDetails(project, container) {
        const template = document.getElementById('project-details-template').content.cloneNode(true);
        
        // Tags
        template.querySelector('.skill-tag').textContent = project.skillLevel;
        template.querySelector('.lang-tag').textContent = project.programmingLanguage;
        template.querySelector('.framework-tag').textContent = project.framework;
        template.querySelector('.domain-tag').textContent = project.projectDomain;
        
        // Basic Info
        template.querySelector('.project-name').textContent = project.projectName;
        template.querySelector('.project-description').textContent = project.projectDescription;
        
        // Lists
        populateList(template.querySelector('.feature-list'), project.keyFeatures);
        populateList(template.querySelector('.tables-list'), project.suggestedTables);
        populateList(template.querySelector('.api-list'), project.recommendedEndpoints);
        populateList(template.querySelector('.roadmap-list'), project.learningRoadmap);
        
        container.innerHTML = '';
        
        // Add a back button if in result view
        if (container.id === 'result-container') {
            const backBtn = document.createElement('button');
            backBtn.className = 'btn btn-primary';
            backBtn.style.marginTop = '2rem';
            backBtn.style.width = 'auto';
            backBtn.innerHTML = '<i class="fa-solid fa-arrow-left"></i> Generate Another';
            backBtn.onclick = () => {
                container.classList.add('hidden');
                generateForm.parentElement.classList.remove('hidden');
                generateForm.reset();
            };
            template.querySelector('.project-details').appendChild(backBtn);
        }

        container.appendChild(template);
    }

    function populateList(ulElement, items) {
        if (!items || items.length === 0) {
            ulElement.innerHTML = '<li>Not specified</li>';
            return;
        }
        
        ulElement.innerHTML = items.map(item => `<li>${item}</li>`).join('');
    }

    function createHistoryCard(project) {
        const div = document.createElement('div');
        div.className = 'glass-inner history-card fade-in-up';
        
        const date = new Date(project.createdAt).toLocaleDateString(undefined, { 
            year: 'numeric', month: 'short', day: 'numeric' 
        });

        div.innerHTML = `
            <div class="tags">
                <span class="tag lang-tag">${project.programmingLanguage}</span>
                <span class="tag domain-tag">${project.projectDomain}</span>
            </div>
            <h3>${project.projectName}</h3>
            <p>${project.projectDescription}</p>
            <span class="date">${date}</span>
        `;

        div.addEventListener('click', () => {
            // Re-use result container to show full details
            document.querySelectorAll('.view').forEach(v => v.classList.remove('active'));
            const generateView = document.getElementById('generate-view');
            generateView.classList.add('active');
            
            document.querySelectorAll('.nav-links a').forEach(l => l.classList.remove('active'));
            document.querySelector('[data-page="generate"]').classList.add('active');

            generateForm.parentElement.classList.add('hidden');
            resultContainer.classList.remove('hidden');
            renderProjectDetails(project, resultContainer);
            
            window.scrollTo({ top: 0, behavior: 'smooth' });
        });

        return div;
    }
});
