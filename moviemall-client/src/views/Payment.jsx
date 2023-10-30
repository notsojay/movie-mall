import {postData} from "../utils/apiCaller";
import {API_PATH} from "../config/servletPaths";
import {useState} from "react";

import '../assets/styles/payment.css'
import {useLocation, useSearchParams} from "react-router-dom";

function Payment() {
    const [searchParams] = useSearchParams();
    const totalAmount = searchParams.get('total-amount');
    const [message, setMessage] = useState(null);
    const [creditCardInfo, setCreditCardInfo] = useState({
        firstName: '',
        lastName: '',
        cardNumber: '',
        expiryDate: ''
    });

    const handleSubmit = async (e) => {
        e.preventDefault();
        try {
            const response = await postData(API_PATH.ORDER, {
                firstName: creditCardInfo.firstName,
                lastName: creditCardInfo.lastName,
                cardNumber: creditCardInfo.cardNumber,
                expiryDate: creditCardInfo.expiryDate
            }, true, 'Error creating order');

            if (response && response.status === "success") {
                setMessage({ type: 'success', text: 'Order has been placed successfully!' });
            } else if (response && response.status === "error") {
                setMessage({ type: 'error', text: 'Error creating order. Please try again.' });
            }
        } catch (error) {
            console.error('Error submitting order:', error);
            setMessage({ type: 'error', text: 'Error submitting order. Please try again.' });
        }
    };

    if (message != null) console.log(message.text);

    return (
        <div className="payment-background">
            <div className="payment-container">
                <h2>Payment Information</h2>
                <form onSubmit={handleSubmit}>
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
                    <div className="final-amount">Final Payment: ${totalAmount}</div>
                    <button type="submit" className="place-order-btn">Place Order</button>
                </form>
                )}
            </div>
        </div>
    );
}

export default Payment;
