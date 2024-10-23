module.exports = {
    content: process.env.NODE_ENV == 'production'
        // in prod look at shadow-cljs output file
        ? ["./public/js/main.js"]
        // in dev look at runtime, which will change files that are actually compiled; postcss watch should be a whole lot faster
        : ["./public/js/cljs-runtime/frontend.*.js"],
    theme: {
        extend: {}
    },
    plugins: [
        require('@tailwindcss/forms'),
    ],
}
