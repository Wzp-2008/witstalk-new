import axios from "axios";
import {showMessage} from "~/util/msg";
import {aesEncrypt, generateAesKeyAndIv, rsaEncrypt} from "~/util/encryption.ts";
import {keyStore} from "~/store/keyStore.ts";
import CryptoJS from 'crypto-js';

axios.defaults.headers['Content-Type'] = 'application/json;charset=utf-8'
// 创建axios实例
const instance = axios.create({
    // axios中请求配置有baseURL选项，表示请求URL公共部分
    baseURL: import.meta.env.VITE_APP_BASE_API,
    // 超时
    timeout: 1000000
})
const responseInterceptors = (response) => {
    const token = response.headers.token;
    if (token) {
        window.localStorage.setItem("token", 'Bearer ' + token);
    }
    return response;
};
const responseInterceptorsError = (error) => {
    if (error.response && error.response.status === 403) {
        showMessage.error("没有权限访问该资源，请联系管理员！")
    } else if (error.response && error.response.status === 500) {

    } else {
        // 其他错误处理
        console.error("请求错误:", error.message);
    }
    return Promise.reject(error);
}
instance.interceptors.response.use(responseInterceptors, responseInterceptorsError);
const requestInterceptors = (request) => {
    // 请求前加密 key 44 iv 24
    // 只有post请求才会加解密
    let flag = request.method.toUpperCase() === "POST" || request.method.toUpperCase() === "PUT";
    if (flag) {
        let data = request.params || request.data
        let key: string | null = '', iv: string | null = ''
        if (keyStore.getState().key2 && keyStore.getState().key3) {
            key = keyStore.getState().key2
            iv = keyStore.getState().key3
        } else {
            const aesKeyAndIv = generateAesKeyAndIv()
            key = aesKeyAndIv.key
            iv = aesKeyAndIv.iv
            keyStore.setState({key2: key, key3: iv})
        }
        console.log(key)
        console.log(iv)
        let encodeRequestData = ""
        if (data) {
            encodeRequestData = aesEncrypt(JSON.stringify(data), key as string, iv as string)
        }
        let encodeKeyIV = rsaEncrypt(key + iv, keyStore.getState().key1 as string)
        console.log(encodeKeyIV)
        request.data = CryptoJS.enc.Base64.stringify(CryptoJS.enc.Utf8.parse(encodeRequestData + encodeKeyIV))
    }
    const token = window.localStorage.getItem("token");
    if (request.headers) {
        request.headers["Authorization"] = token;
    } else {
        request.headers = {"Authorization": token};
    }
    return request;
};
instance.interceptors.request.use(requestInterceptors);

export default instance;
