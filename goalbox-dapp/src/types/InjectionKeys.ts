import { InjectionKey } from "vue";
import Web3 from "web3";

export default class InjectionKeys {
    public static readonly Web3: InjectionKey<Web3> = Symbol('Web3');
}