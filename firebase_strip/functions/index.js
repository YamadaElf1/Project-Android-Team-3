const functions = require("firebase-functions");


const stripe = require('stripe')('sk_test_51QLxCWKDqkg4Sulo5taCPJVdRFvmdUfSNNXViF4AicbxeERrLuiUYpH1xmDEtn9IJB3QfO64QDuWnGZPvFyqZJ3u00TOKOMAAy');

exports.stripPayment = functions.https.onRequest(async (request, response) => {
    const amount = request.query.amt;
  
    const customer = await stripe.customers.create();
    const ephemeralKey = await stripe.ephemeralKeys.create(
      { customer: customer.id },
      { apiVersion: '2024-10-28.acacia' }
    );
    const paymentIntent = await stripe.paymentIntents.create({
      amount: amount,
      currency: 'cad',
      customer: customer.id,
      automatic_payment_methods: {
        enabled: true,
      },
    });
  
    response.json({
      paymentIntent: paymentIntent.client_secret,
      ephemeralKey: ephemeralKey.secret,
      customer: customer.id,
      publishableKey: 'pk_test_51QLxCWKDqkg4SuloFBMj59BuVnmSmLrMZlB1hyaaWn5XZ8eSOw8mGHbI9NFf9rlZled9fgAsYxr4tPPUELgMNfTY00kZcBFfC9'
    });
  });
  