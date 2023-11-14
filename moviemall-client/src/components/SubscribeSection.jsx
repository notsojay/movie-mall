// React component
import React, {useEffect} from 'react';
import '../assets/styles/subscribe-section.css'; // Make sure to create a CSS file with this name

const SubscribeSection = () => {

    // useEffect(() => {
    //     const handleScroll = () => {
    //         const element = document.querySelector('.subscribe-section');
    //         const scrollTotal = window.innerHeight + window.scrollY;
    //         const documentHeight = document.body.offsetHeight;
    //
    //         if (scrollTotal < documentHeight) {
    //             element.style.transform = 'translateY(0)'; // Shows the element when user scrolls down
    //         } else {
    //             element.style.transform = 'translateY(100%)'; // Hides the element when scrolled to the bottom
    //         }
    //     };
    //
    //     // Attach the handler to the scroll event
    //     window.addEventListener('scroll', handleScroll);
    //
    //     // Clean up the listener when the component unmounts
    //     return () => window.removeEventListener('scroll', handleScroll);
    // }, []);

    return (
        <div className="subscribe-section">
            <header>
                <h2>Follow us on social for updates.</h2>
            </header>
            <div className="social-media-icon-container">
                <a href="https://github.com/notsojay" target="_blank" rel="noopener noreferrer">
                    <img className="social-media-icon" src={process.env.PUBLIC_URL + '/imgs/github.png'} alt="Github" />
                </a>
                <a href="https://www.instagram.com/notsojay/" target="_blank" rel="noopener noreferrer">
                    <img className="social-media-icon" src={process.env.PUBLIC_URL + '/imgs/instagram.png'} alt="Instagram" />
                </a>
            </div>
            <div className="subscription-box">
                <h3>Subscribe</h3>
                <p className="sign-up-text">Sign up to be the first to know about our soft launch events.</p>
                <input type="email" placeholder="Email" />
                <button className="subscribe-section-button">Sign Up</button>
            </div>
            <footer className="subscribe-section-footer">
                <p>Copyright © 2023 movie-mall.com - Tutti i diritti riservati.</p>
                <p>Contributors: Jiahao Liang, Xiaohua Zhang
                </p>
                <p>MOVIE-MALL</p>
            </footer>
        </div>
    );
};

export default SubscribeSection;
