document.addEventListener('DOMContentLoaded', () => {
    const selects = document.querySelectorAll('select');
    
    selects.forEach(originalSelect => {
        // Hide the original select visually but keep it in the DOM for HTML5 validation
        originalSelect.style.opacity = '0';
        originalSelect.style.position = 'absolute';
        originalSelect.style.pointerEvents = 'none';
        originalSelect.style.width = '100%';
        originalSelect.style.height = '100%';
        originalSelect.style.top = '0';
        originalSelect.style.left = '0';
        originalSelect.style.zIndex = '-1';
        
        // Create wrapper
        const wrapper = document.createElement('div');
        wrapper.className = 'custom-select-wrapper';
        wrapper.style.position = 'relative';
        originalSelect.parentNode.insertBefore(wrapper, originalSelect);
        wrapper.appendChild(originalSelect); // Move the select inside wrapper
        
        // Create display element
        const display = document.createElement('div');
        display.className = 'custom-select-display';
        display.innerHTML = `<span></span> <i class="fa-solid fa-chevron-down"></i>`;
        wrapper.appendChild(display);
        
        // Create options container
        const optionsContainer = document.createElement('div');
        optionsContainer.className = 'custom-select-options';
        wrapper.appendChild(optionsContainer);
        
        // Function to build options
        function buildOptions() {
            optionsContainer.innerHTML = '';
            
            // Get selected text for display
            let selectedText = '';
            
            Array.from(originalSelect.options).forEach(opt => {
                const optDiv = document.createElement('div');
                optDiv.className = 'custom-option';
                optDiv.textContent = opt.textContent;
                
                if (opt.disabled) {
                    optDiv.classList.add('disabled');
                    if (opt.selected) selectedText = opt.textContent;
                } else {
                    optDiv.dataset.value = opt.value;
                    if (opt.selected) {
                        optDiv.classList.add('selected');
                        selectedText = opt.textContent;
                    }
                    
                    optDiv.addEventListener('click', () => {
                        originalSelect.value = opt.value;
                        // Trigger change event for app.js to catch
                        originalSelect.dispatchEvent(new Event('change', { bubbles: true }));
                        
                        // Close dropdown
                        optionsContainer.classList.remove('open');
                        display.classList.remove('open');
                        
                        // Update selection visually
                        Array.from(optionsContainer.children).forEach(c => c.classList.remove('selected'));
                        optDiv.classList.add('selected');
                        display.querySelector('span').textContent = opt.textContent;
                        display.querySelector('span').style.color = '#2B303A';
                    });
                }
                
                optionsContainer.appendChild(optDiv);
            });
            
            if (selectedText) {
                display.querySelector('span').textContent = selectedText;
                if (originalSelect.options[originalSelect.selectedIndex] && originalSelect.options[originalSelect.selectedIndex].disabled) {
                    display.querySelector('span').style.color = '#A0AAB5'; // placeholder color
                } else {
                    display.querySelector('span').style.color = '#2B303A';
                }
            }
        }
        
        buildOptions();
        
        // Toggle dropdown
        display.addEventListener('click', (e) => {
            e.stopPropagation();
            // Close all others
            document.querySelectorAll('.custom-select-options.open').forEach(optMenu => {
                if (optMenu !== optionsContainer) {
                    optMenu.classList.remove('open');
                    optMenu.previousElementSibling.classList.remove('open');
                }
            });
            optionsContainer.classList.toggle('open');
            display.classList.toggle('open');
        });
        
        // Observe mutations on the original select (for when app.js updates frameworks/domains)
        const observer = new MutationObserver((mutations) => {
            buildOptions();
        });
        observer.observe(originalSelect, { childList: true, subtree: true });
    });
    
    // Close dropdowns when clicking outside
    document.addEventListener('click', () => {
        document.querySelectorAll('.custom-select-options.open').forEach(optMenu => {
            optMenu.classList.remove('open');
            optMenu.previousElementSibling.classList.remove('open');
        });
    });
});
