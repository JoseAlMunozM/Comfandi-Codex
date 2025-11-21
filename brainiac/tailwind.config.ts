import type { Config } from 'tailwindcss';

const config: Config = {
  content: ['./src/**/*.{js,ts,jsx,tsx}'],
  theme: {
    extend: {
      colors: {
        'blue-text': '#003DA5',
        'blue-button': '#005EAA',
        'blue-dropzone': '#9EC3FF1A',
        'blue-title-modal': '#003C6A',
        'green-button': '#97D700',
        'border-gray': '#979797',
        'border-dropzone': '#D1D0D0',
        'text-modal': '#302F30',
        'title-modal': '#555555',
        'letter-gray': '#777777',
        'black-card': '#2C2C2C',
        'black-table': '#1F1F1F',
        principal: {
          150: "#FFFFFF", //Blanco
          50: "#D8D8D8", //Gris chips
          80: "#05C3DD", //Azul claro logo
          100: "#005EAA", //Azul general
          110: "#DCEDFF", //Azul claro
          120: "#2170B6", //Azul radio
          180: "#003DA5", // Azul sidebar
          190: "#EBEFF8", // Azul cards
          200: "#F7F8FC", //Fondo pantallas
          250: "#ECECEC", //Gris tarjetas info
          300: "#E7E7E7 ", //Gris Hover
          320: "#777", // Texto gris
          330: "#979797", // Borde inputs
          350: "#333333", //Texto negro
          400: "#C7D2E6", //Gris azulado
          450: "#777777", //Gris texto pequeño
          500: "#FD536D", //red trash
          520: "#E83720", //red bright
          550: "#ECECEC",
          600: "#D7D7D7", //Gris separadores
          650: "#F9FAFB", // Fondo gris body
          680: "#F6F6F6", // Gris
          700: "#97D700", // Verde
          725: "#05C3DD", // Section color
          750: "#01C4AE", // IPS fondo
          800: "#313131A6", //Modal background
          900: "#D1D0D0", //Gris dropzone
          950: "#9EC3FF3D",
        },
      },
      dropShadow: {
        '3xl': '0 9px 10px rgba(0, 60, 106, 0.07)',
      },
    },    
  },
  plugins: [],
};
export default config;
