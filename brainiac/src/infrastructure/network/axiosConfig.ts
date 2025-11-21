import axios from 'axios';

const nextApiURL = process.env.NEXT_PUBLIC_SITE_URL;
const nextBackEndURL = process.env.NEXT_PUBLIC_SITE_API;
const api_user = process.env.NEXT_PUBLIC_PHOBOS_AUTH_USER?process.env.NEXT_PUBLIC_PHOBOS_AUTH_USER:"null";
const api_pass = process.env.NEXT_PUBLIC_PHOBOS_AUTH_PASS?process.env.NEXT_PUBLIC_PHOBOS_AUTH_PASS:"null";

const NextAxiosInstance = axios.create({ baseURL: nextApiURL });
const NextAxiosBackEnd = axios.create({ baseURL: nextBackEndURL, auth: {
          username: api_user,
          password: api_pass,
        },
        withCredentials: true
     });

export { NextAxiosInstance, NextAxiosBackEnd };
