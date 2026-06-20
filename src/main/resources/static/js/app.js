document.addEventListener('DOMContentLoaded', () => {
    // Session Management
    let sessionId = localStorage.getItem('project_idea_session_id');
    if (!sessionId) {
        sessionId = 'session_' + Math.random().toString(36).substr(2, 9) + Date.now().toString(36);
        localStorage.setItem('project_idea_session_id', sessionId);
    }

    const navLinks = document.querySelectorAll('.nav-links a');
    const views = document.querySelectorAll('.view');
    const generateForm = document.getElementById('generate-form');
    const loadingState = document.getElementById('loading-state');
    const resultContainer = document.getElementById('result-container');
    const historyLoading = document.getElementById('history-loading');
    const historyGrid = document.getElementById('history-grid');
    const programmingLanguageSelect = document.getElementById('programmingLanguage');
    const frameworkSelect = document.getElementById('framework');
    const API_BASE_URL = window.location.protocol === 'file:' 
        ? 'http://localhost:8080/api/projects' 
        : '/api/projects';
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
        updateDomains();
    }

    const projectDomainSelect = document.getElementById('projectDomain');
    function updateDomains() {
        const lang = programmingLanguageSelect.value;
        const fw = frameworkSelect.value;
        
        let ragOption = Array.from(projectDomainSelect.options).find(opt => opt.value === 'RAG');
        
        if (lang === 'Java' && fw === 'Spring Boot') {
            if (!ragOption) {
                const option = document.createElement('option');
                option.value = 'RAG';
                option.textContent = 'RAG (Retrieval-Augmented Generation)';
                projectDomainSelect.appendChild(option);
            }
        } else {
            if (ragOption) {
                if (projectDomainSelect.value === 'RAG') {
                    projectDomainSelect.value = '';
                }
                ragOption.remove();
            }
        }
    }

    programmingLanguageSelect.addEventListener('change', updateFrameworks);
    frameworkSelect.addEventListener('change', updateDomains);
    
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
            const targetPage = link.getAttribute('data-page');
            
            // If the link does not have a data-page, let the browser handle it normally (like the Admin link)
            if (!targetPage) return;
            
            e.preventDefault();
            
            // Update active link
            navLinks.forEach(l => l.classList.remove('active'));
            link.classList.add('active');
            
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

        let clientUserAgent = navigator.userAgent;
        if (navigator.userAgentData && navigator.userAgentData.getHighEntropyValues) {
            try {
                const ua = await navigator.userAgentData.getHighEntropyValues(["model"]);
                if (ua.model) {
                    clientUserAgent = `${navigator.userAgent} [Model: ${ua.model}]`;
                }
            } catch (err) {
                console.error("Error getting client hints", err);
            }
        }

        try {
            const response = await fetch(`${API_BASE_URL}/generate`, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                    'X-Session-Id': sessionId,
                    'X-Client-User-Agent': clientUserAgent
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
            
            // Increment generation count and prompt for feedback
            if (typeof generationCount !== 'undefined') {
                generationCount++;
                sessionStorage.setItem('generationCount', generationCount);
                if (generationCount === 3 && !sessionStorage.getItem('feedbackPrompted')) {
                    setTimeout(() => {
                        if (typeof openFeedbackModal === 'function') {
                            openFeedbackModal();
                            sessionStorage.setItem('feedbackPrompted', 'true');
                        }
                    }, 1500);
                }
            }
            
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
            const response = await fetch(`${API_BASE_URL}/history`, {
                headers: {
                    'X-Session-Id': sessionId
                }
            });
            if (!response.ok) throw new Error('Failed to fetch history');

            const data = await response.json();
            historyLoading.classList.add('hidden');

            if (data.message === "no history" || data.length === 0) {
                historyGrid.innerHTML = '<p style="color: var(--text-muted); text-align: center; grid-column: 1/-1;">no history</p>';
                return;
            }

            const projects = Array.isArray(data) ? data : (data.data || []);
            
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
        
        // Add action buttons if in result view
        if (container.id === 'result-container') {
            const buttonsContainer = document.createElement('div');
            buttonsContainer.style.display = 'flex';
            buttonsContainer.style.gap = '1rem';
            buttonsContainer.style.marginBottom = '2rem';
            buttonsContainer.style.flexWrap = 'wrap';

            const backBtn = document.createElement('button');
            backBtn.className = 'btn btn-secondary';
            backBtn.style.width = 'auto';
            backBtn.innerHTML = '<i class="fa-solid fa-arrow-left"></i> Start Over';
            backBtn.onclick = () => {
                container.classList.add('hidden');
                generateForm.parentElement.classList.remove('hidden');
                generateForm.reset();
                lastLanguage = '';
                frameworkSelect.innerHTML = '<option value="" disabled selected>Select Language First</option>';
                updateDomains();
            };

            const regenerateBtn = document.createElement('button');
            regenerateBtn.className = 'btn btn-primary';
            regenerateBtn.style.width = 'auto';
            
            let timeLeft = 10;
            regenerateBtn.disabled = true;
            regenerateBtn.style.cursor = 'not-allowed';
            regenerateBtn.innerHTML = `<i class="fa-solid fa-rotate-right"></i> Regenerate Idea (${timeLeft}s)`;
            
            const timerInterval = setInterval(() => {
                timeLeft--;
                if (timeLeft > 0) {
                    regenerateBtn.innerHTML = `<i class="fa-solid fa-rotate-right"></i> Regenerate Idea (${timeLeft}s)`;
                } else {
                    clearInterval(timerInterval);
                    regenerateBtn.disabled = false;
                    regenerateBtn.style.cursor = 'pointer';
                    regenerateBtn.innerHTML = `<i class="fa-solid fa-rotate-right"></i> Regenerate Idea`;
                }
            }, 1000);

            regenerateBtn.onclick = () => {
                if (!regenerateBtn.disabled) {
                    generateForm.dispatchEvent(new Event('submit', { cancelable: true, bubbles: true }));
                }
            };

            const downloadPdfBtn = document.createElement('button');
            downloadPdfBtn.className = 'btn btn-secondary';
            downloadPdfBtn.style.width = 'auto';
            downloadPdfBtn.innerHTML = '<i class="fa-solid fa-file-pdf"></i> Download PDF';
            downloadPdfBtn.onclick = () => {
                const element = document.querySelector('.project-details');
                
                // Configure PDF options
                const opt = {
                    margin:       [0.5, 0.5], // 0.5 inch margins
                    filename:     `${project.projectName.replace(/[^a-zA-Z0-9]/g, '_')}_Idea.pdf`,
                    image:        { type: 'jpeg', quality: 1.0 },
                    html2canvas:  { scale: 2, useCORS: true, backgroundColor: '#ffffff' },
                    jsPDF:        { unit: 'in', format: 'a4', orientation: 'portrait' },
                    pagebreak:    { mode: ['avoid-all', 'css', 'legacy'] }
                };
                
                // Hide buttons before PDF generation and add PDF specific styling class
                buttonsContainer.style.display = 'none';
                element.classList.add('pdf-export');
                
                html2pdf().set(opt).from(element).save().then(() => {
                    // Revert UI changes after generation
                    buttonsContainer.style.display = 'flex';
                    element.classList.remove('pdf-export');
                });
            };

            buttonsContainer.appendChild(backBtn);
            buttonsContainer.appendChild(regenerateBtn);
            buttonsContainer.appendChild(downloadPdfBtn);
            template.querySelector('.project-details').prepend(buttonsContainer);
            
            if (!sessionStorage.getItem('duplicateWarningShown')) {
                const warningMsg = document.createElement('div');
                warningMsg.className = 'glass-inner';
                warningMsg.style.backgroundColor = 'rgba(245, 158, 11, 0.05)';
                warningMsg.style.border = '1px solid rgba(245, 158, 11, 0.2)';
                warningMsg.style.borderLeft = '4px solid #f59e0b';
                warningMsg.style.padding = '1rem';
                warningMsg.style.marginBottom = '1.5rem';
                warningMsg.style.borderRadius = '0.5rem';
                warningMsg.style.display = 'flex';
                warningMsg.style.justifyContent = 'space-between';
                warningMsg.style.alignItems = 'center';
                warningMsg.innerHTML = `
                    <div>
                        <i class="fa-solid fa-circle-info" style="color: #f59e0b; margin-right: 8px;"></i>
                        <span style="color: var(--text-light);"><strong>Note:</strong> Sometimes the AI might give a similar result. If that happens, just click <strong>Regenerate Idea</strong> for a fresh one!</span>
                    </div>
                    <i class="fa-solid fa-xmark" style="cursor: pointer; color: var(--text-muted);" onclick="this.parentElement.style.display='none'"></i>
                `;
                template.querySelector('.project-details').prepend(warningMsg);
                sessionStorage.setItem('duplicateWarningShown', 'true');
            }
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

    // Feedback Logic
    const feedbackModal = document.getElementById('feedback-modal');
    const floatingFeedbackBtn = document.getElementById('floating-feedback-btn');
    const closeFeedback = document.querySelector('.close-modal');
    const stars = document.querySelectorAll('.star-rating i');
    const ratingInput = document.getElementById('rating-value');
    const feedbackForm = document.getElementById('feedback-form');
    const feedbackSuccess = document.getElementById('feedback-success');
    
    window.generationCount = parseInt(sessionStorage.getItem('generationCount') || '0');

    window.openFeedbackModal = function() {
        feedbackModal.classList.remove('hidden');
        feedbackSuccess.classList.add('hidden');
        feedbackForm.style.display = 'block';
        feedbackForm.reset();
        ratingInput.value = '0';
        updateStars(0);
    }

    floatingFeedbackBtn.addEventListener('click', openFeedbackModal);
    closeFeedback.addEventListener('click', () => feedbackModal.classList.add('hidden'));

    stars.forEach(star => {
        star.addEventListener('click', (e) => {
            const rating = parseInt(e.target.getAttribute('data-rating'));
            ratingInput.value = rating;
            updateStars(rating);
        });
        
        star.addEventListener('mouseover', (e) => {
            updateStars(parseInt(e.target.getAttribute('data-rating')));
        });
        
        star.addEventListener('mouseout', () => {
            updateStars(parseInt(ratingInput.value));
        });
    });

    function updateStars(rating) {
        stars.forEach(star => {
            if (parseInt(star.getAttribute('data-rating')) <= rating) {
                star.classList.remove('fa-regular');
                star.classList.add('fa-solid');
                star.classList.add('active');
            } else {
                star.classList.remove('fa-solid');
                star.classList.add('fa-regular');
                star.classList.remove('active');
            }
        });
    }

    feedbackForm.addEventListener('submit', async (e) => {
        e.preventDefault();
        
        const feedbackData = {
            stars: parseInt(ratingInput.value),
            email: document.getElementById('feedback-email').value,
            message: document.getElementById('feedback-message').value
        };

        if (feedbackData.stars === 0) {
            alert('Please select a star rating');
            return;
        }

        const feedbackApiUrl = API_BASE_URL.replace('/projects', '/feedback');

        try {
            const response = await fetch(feedbackApiUrl, {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify(feedbackData)
            });

            if (response.ok) {
                feedbackForm.style.display = 'none';
                feedbackSuccess.classList.remove('hidden');
                setTimeout(() => {
                    feedbackModal.classList.add('hidden');
                }, 2000);
            } else {
                alert('Failed to submit feedback');
            }
        } catch (e) {
            console.error(e);
            alert('Error submitting feedback');
        }
    });

});
