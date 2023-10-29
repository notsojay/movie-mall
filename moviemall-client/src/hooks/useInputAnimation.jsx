import {useState} from 'react';

function useInputAnimation() {
    const [isActive, setIsActive] = useState(false);

    const animateInput = () => {
        setIsActive(true);
        setTimeout(() => {
            setIsActive(false);
        }, 3000);
    };

    return [animateInput, setIsActive, isActive];
}

export default useInputAnimation;
