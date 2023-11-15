import {postData} from "../utils/apiCaller";
import {SERVLET_ROUTE} from "../config/servletRoutes";
import React, {useEffect, useRef, useState} from "react";

import '../assets/styles/payment-form.css'
import {useLocation, useSearchParams} from "react-router-dom";
import ReCAPTCHA from "react-google-recaptcha";

function PaymentForm({ totalAmount, onClose }) {
    const [searchParams] = useSearchParams();
    const [message, setMessage] = useState(null);
    const [captchaValue, setCaptchaValue] = useState(null);
    const recaptchaRef = useRef(null);
    const [creditCardInfo, setCreditCardInfo] = useState({
        firstName: '',
        lastName: '',
        cardNumber: '',
        expiryDate: ''
    });

    const resetCaptcha = () => {
        if (recaptchaRef.current) {
            recaptchaRef.current.reset();
        }
        setCaptchaValue(null);
    };

    const handleSubmit = async (event) => {
        event.preventDefault();

        if (!captchaValue) {
            setMessage({ type: 'error', text: 'Please verify that you are not a robot.' });
            return;
        }

        await postData(SERVLET_ROUTE.ORDER, {
            firstName: creditCardInfo.firstName,
            lastName: creditCardInfo.lastName,
            cardNumber: creditCardInfo.cardNumber,
            cardExpiryDate: creditCardInfo.expiryDate,
            captchaValue: captchaValue
        }, true, 'Error creating order')
            .then(response => {
                if (response?.status === 200) {
                    setMessage({ type: 'success', text: 'Order has been placed successfully!' });
                }
            })
            .catch(error => {
                console.error('Error submitting order:', error);
                resetCaptcha();
                setMessage({ type: 'error', text: 'Error submitting order. Please try again.' });
            });
    };

    const handleCaptchaChange = (value) => {
        setCaptchaValue(value);
    };

    if (message != null) console.log(message.text);

    return (
        <div className="payment-background">
            <div className="payment-container">
                <div className="inner-scroll-container">
                <form className={"payment-form"} onSubmit={handleSubmit}>
                    <button className="closeModalX" onClick={onClose}>X</button>
                    {message && (
                            <div className={`message ${message.type}`}>
                                {message.text}
                            </div>
                        )
                    }
                    <div className="form-group">
                        <label>First Name</label>
                        <input
                            type="text"
                            value={creditCardInfo.firstName}
                            onChange={(e) => setCreditCardInfo({ ...creditCardInfo, firstName: e.target.value })}
                        />
                    </div>
                    <div className="form-group">
                        <label>Last Name</label>
                        <input
                            type="text"
                            value={creditCardInfo.lastName}
                            onChange={(e) => setCreditCardInfo({ ...creditCardInfo, lastName: e.target.value })}
                        />
                    </div>
                    <div className="form-group">
                        <label>Card Number</label>
                        <input
                            type="text"
                            value={creditCardInfo.cardNumber}
                            onChange={(e) => setCreditCardInfo({ ...creditCardInfo, cardNumber: e.target.value })}
                        />
                    </div>
                    <div className="form-group">
                        <label>Expiry Date (mm/dd/yyyy)</label>
                        <input
                            type="text"
                            value={creditCardInfo.expiryDate}
                            onChange={(e) => setCreditCardInfo({ ...creditCardInfo, expiryDate: e.target.value })}
                        />
                    </div>
                    <div className="final-amount">Final Payment: ${totalAmount.toFixed(2)}</div>
                    <ReCAPTCHA
                        ref={recaptchaRef}
                        sitekey={process.env.REACT_APP_RECAPTCHA_SITE_KEY}
                        onChange={handleCaptchaChange}
                        className="payment-recaptcha"
                        theme="dark"
                    />
                    <button type="submit" className="place-order-btn">Place Order</button>
                </form>
                )}
                </div>
            </div>
        </div>
    );
}

export default PaymentForm;
