/** @type {import('tailwindcss').Config} */
export default {
  content: ["./index.html", "./src/**/*.{ts,tsx}"],
  theme: {
    extend: {
      colors: {
        cream: "#FAF4EC",
        warm: "#FFFBF5",
        polaroid: "#FFFCF6",
        terracotta: {
          DEFAULT: "#C76B4A",
          deep: "#A04E33",
          light: "#D17A5C",
        },
        dusty: "#E8B4A0",
        blush: "#F4DBCF",
        washi: "#D4A574",
        sage: "#B8C4A8",
        ink: {
          DEFAULT: "#3D2E26",
          mocha: "#6B5544",
          soft: "#8B7560",
          faded: "#B5A696",
        },
      },
      fontFamily: {
        serif: ['"Fraunces"', '"Cormorant Garamond"', "Georgia", "serif"],
        sans: ['"Inter"', "ui-sans-serif", "system-ui", "sans-serif"],
        script: ['"Caveat"', '"Dancing Script"', "cursive"],
      },
      boxShadow: {
        polaroid: "0 8px 24px -6px rgba(61, 46, 38, 0.18), 0 2px 6px -2px rgba(61, 46, 38, 0.12)",
        soft: "0 6px 18px -4px rgba(61, 46, 38, 0.14)",
        phone: "0 30px 60px -20px rgba(61, 46, 38, 0.45), 0 12px 30px -8px rgba(61, 46, 38, 0.25)",
      },
      backgroundImage: {
        "terracotta-gradient": "linear-gradient(90deg, #D17A5C 0%, #C76B4A 50%, #B35A3D 100%)",
        "warm-glow": "radial-gradient(ellipse at top, rgba(232, 180, 160, 0.35) 0%, transparent 60%)",
        "k-badge": "linear-gradient(135deg, #C76B4A 0%, #E8B4A0 100%)",
      },
      keyframes: {
        speckle: { "0%, 100%": { opacity: "0.6" }, "50%": { opacity: "0.9" } },
        floaty: { "0%, 100%": { transform: "translateY(0)" }, "50%": { transform: "translateY(-6px)" } },
        shimmer: { "0%": { backgroundPosition: "-200% 0" }, "100%": { backgroundPosition: "200% 0" } },
      },
      animation: {
        floaty: "floaty 6s ease-in-out infinite",
        shimmer: "shimmer 2.4s linear infinite",
      },
    },
  },
  plugins: [],
};
