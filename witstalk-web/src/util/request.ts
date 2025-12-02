import requesterAxios from "~/util/requesterAxios";
import useSWR from "swr";
import type {AxiosRequestConfig} from "axios";

/**
 * 适用于swr的请求体参数
 */
export interface requestParams {
    url: string,
    method: string,
    data?: any,
}

/**
 * swr请求器封装
 * @param param
 */
export const requestSWR = (param: requestParams) => {
    return useSWR([param.url, param], ([url, param]) => axios(url, param));
}

/**
 * axios请求器封装
 * @param config axios原生请求配置
 */
export const request = (config: AxiosRequestConfig) => {
    return requesterAxios(config);
}

/**
 * 适配于swr的请求器fetch
 * @param url 地址
 * @param param 参数
 */
export const axios = (url: string, param: requestParams) => {
    let data = {
        url: url,
        method: param.method,
        data: null,
        params: null
    }
    if (param.method.toUpperCase() === "GET" || param.method.toUpperCase() === "PUT" ) {
        data['params'] = param.data;
    } else {
        data['data'] = param.data;
    }
    return requesterAxios(data).then(res => res.data)
}
