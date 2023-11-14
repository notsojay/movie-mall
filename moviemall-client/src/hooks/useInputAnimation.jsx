import {useState} from 'react';

function useInputAnimation() {
    const [isAnimationActive, setIsAnimationActive] = useState(false);

    const animateInput = () => {
        setIsAnimationActive(true);
        setTimeout(() => {
            setIsAnimationActive(false);
        }, 3000);
    };

    return [animateInput, isAnimationActive, setIsAnimationActive];
}

export default useInputAnimation;
