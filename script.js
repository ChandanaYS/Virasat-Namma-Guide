const menuToggle = document.querySelector(".menu-toggle");
const navLinks = document.querySelector(".nav-links");

if (menuToggle && navLinks) {
    menuToggle.addEventListener("click", () => {
        navLinks.classList.toggle("open");
    });

    navLinks.querySelectorAll("a").forEach((link) => {
        link.addEventListener("click", () => {
            navLinks.classList.remove("open");
        });
    });
}

const animatedSections = document.querySelectorAll(
    ".intro-section, .features-section, .screenshots-section, .highlight-section, .technology-section, .developer-section"
);

animatedSections.forEach((section) => {
    section.classList.add("reveal");
});

const observer = new IntersectionObserver(
    (entries) => {
        entries.forEach((entry) => {
            if (entry.isIntersecting) {
                entry.target.classList.add("visible");
            }
        });
    },
    {
        threshold: 0.14
    }
);

animatedSections.forEach((section) => {
    observer.observe(section);
});
