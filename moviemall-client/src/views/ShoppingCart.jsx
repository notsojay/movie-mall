import React, { useState, useEffect } from 'react';
import {fetchData, postData} from "../utils/apiCaller";
import {SERVLET_ROUTE} from "../config/servletRoutes";

import '../assets/styles/page.css';
import '../assets/styles/table.css';
import '../assets/styles/payment-form.css'
import '../assets/styles/link.css'
import {Link} from "react-router-dom";
import PaymentForm from "../components/PaymentForm";

function ShoppingCart() {
    const [shoppingCart, setShoppingCart] = useState(null);
    const [totalAmount, setTotalAmount] = useState(0);
    const [showPaymentModal, setShowPaymentModal] = useState(false);
    const [isLoading, setIsLoading] = useState(true);
    const [error, setError] = useState(null);

    const updateToCart = (movieId, movieTitle, moviePrice, quantity) => {

        postData(SERVLET_ROUTE.SHOPPING_CART, {
            movieId: movieId,
            movieTitle: movieTitle,
            moviePrice: moviePrice,
            quantity: quantity
        }, false, 'Error adding movie to cart')
            .then(response => {
                if (response.status === 200) {
                    setShoppingCart(response?.data);
                    const total = response.data?.reduce((acc, item) => acc + (item.moviePrice * item.quantity), 0);
                    setTotalAmount(total);
                }
            })
            .catch(err => setError(err.message))
            .finally(() => setIsLoading(false));
    };

    useEffect(() => {
        fetchData(SERVLET_ROUTE.SHOPPING_CART, null, true, "Error fetching cart total")
            .then(response => {
                if (response.status === 200) {
                    setShoppingCart(response.data);
                    if (response.data != null) {
                        const total = response.data.reduce((acc, item) => acc + (item.moviePrice * item.quantity), 0);
                        setTotalAmount(total);
                    }
                } else {
                    throw new Error('Failed to fetch shopping cart: status code ' + response.status);
                }
            })
            .catch(err => setError(err.message))
            .finally(() => setIsLoading(false));
    }, []);

    useEffect(() => {
        if (Array.isArray(shoppingCart)) {
            const total = shoppingCart.reduce((acc, item) => acc + (item.moviePrice * item.quantity), 0);
            setTotalAmount(total);
        }
    }, [shoppingCart]);

    if (!shoppingCart) {
        return null;
    }

    return (
        <div className="page">
            <React.Fragment>
                <h1>Shopping Cart</h1>
                <table>
                    <thead>
                    <tr>
                        <th>Title</th>
                        <th>Quantity</th>
                        <th>Delete All</th>
                        <th>Price</th>
                        <th>Total Amount</th>
                    </tr>
                    </thead>
                    <tbody>
                    {shoppingCart && Array.isArray(shoppingCart) && shoppingCart?.map((item) => (
                        <tr key={item.movieId}>
                            <td>{item.movieTitle}</td>
                            <td>
                                <button className="quantity-custom-button"
                                    onClick={() => updateToCart(item.movieId, item.movieTitle, item.moviePrice, -1)}>
                                    --
                                </button>
                                {item.quantity}
                                <button className="quantity-custom-button"
                                    onClick={() => updateToCart(item.movieId, item.movieTitle, item.moviePrice, 1)}>
                                    +
                                </button>
                            </td>
                            <td>
                                <button className="cart-custom-button"
                                        onClick={() => updateToCart(item.movieId, item.movieTitle, item.moviePrice, item.quantity*(-1))}>
                                    delete
                                </button>
                            </td>
                            <td>{`$${item.moviePrice.toFixed(2)}`}</td>
                            <td>{`$${(item.moviePrice * item.quantity).toFixed(2)}`}</td>
                        </tr>
                    ))}
                        <tr>
                            <td></td>
                            <td></td>
                            <td></td>
                            <td></td>
                            <td className="total-amount">{`$${totalAmount.toFixed(2)}`}</td>
                        </tr>
                    </tbody>
                </table>
                {/*<Link className="payment-custom-button" to={`${APP_ROUTES.PAYMENT}?total-amount=${totalAmount}`}>*/}
                {/*    Process payment*/}
                {/*</Link>*/}
                <Link className="payment-custom-button" onClick={(e) => { e.preventDefault(); setShowPaymentModal(true); }} to="#">
                    Process payment
                </Link>
                {showPaymentModal && <PaymentForm totalAmount={totalAmount} onClose={() => setShowPaymentModal(false)} />}
            </React.Fragment>
        </div>
    );
}


export default ShoppingCart;
