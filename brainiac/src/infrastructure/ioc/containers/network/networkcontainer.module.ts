import { ContainerModule, interfaces } from 'inversify';
import { NETWORK_TYPES } from './network.types';
import { AxiosInstance } from 'axios';
import { NextAxiosBackEnd, NextAxiosInstance,  } from '@/infrastructure/network/axiosConfig';

export const networkModule = new ContainerModule((bind: interfaces.Bind) => {
  bind<AxiosInstance>(NETWORK_TYPES._AxiosNextInstance).toConstantValue(NextAxiosInstance);
  bind<AxiosInstance>(NETWORK_TYPES._AxiosBackEndInstance).toConstantValue(NextAxiosBackEnd)
});
