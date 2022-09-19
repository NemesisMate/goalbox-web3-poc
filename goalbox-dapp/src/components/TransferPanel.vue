<template>
    <v-card>
        <v-card-title class="text-h5">
            Transfer ETH
        </v-card-title>

        <v-card-text>
            <v-container>
                <v-row justify="center">
                    <v-text-field label="Amount" type="number" suffix="ETH" v-model="selectedAmount" required />
                </v-row>
                <v-row justify="center">
                    <v-select :items="availableGoalboxes.filter(i => i !== selectedTarget)" item-title="name"
                        item-value="contractAddress" label="Source address" prefix="From" v-model="selectedSource"
                        return-object required />
                </v-row>
                <v-row justify="center">
                    <v-select :items="availableGoalboxes.filter(i => i !== selectedSource)" item-title="name"
                        item-value="contractAddress" label="Target address" prefix="To" v-model="selectedTarget"
                        return-object required />
                </v-row>
            </v-container>
            <v-btn color="primary" text @click="transfer()">Transfer</v-btn>
        </v-card-text>
    </v-card>
</template>

<script lang='ts'>
import { defineComponent, inject } from 'vue';
import type { PropType } from 'vue';
import Web3 from 'web3';
import { Goalbox } from '../models/Goalbox';
import abiUrl from '../contracts/GoalboxContract.abi?url';
import { Contract } from 'web3-eth-contract';
import InjectionKeys from '../types/InjectionKeys';

let EMPTY: Goalbox;
let web3: Web3;

export default defineComponent({
    name: 'TransferPanel',

    setup() {
        let w3 = inject<Web3>(InjectionKeys.Web3);
        if (!w3?.eth) {
            throw Error();
        }
        web3 = w3;
    },


    props: {
        accountAddress: { type: String, required: true },

        source: { type: Object as PropType<Goalbox> },
        target: { type: Object as PropType<Goalbox> },
        amount: { type: Object as PropType<BigInt> },

        availableGoalboxes: { type: Object as PropType<Goalbox[]>, required: true },
    },


    data() {
        return {
            selectedSource: EMPTY,
            selectedTarget: EMPTY,
            selectedAmount: BigInt(1),

            abi: undefined as any,
        }
    },

    methods: {
        loadGoalboxContract(): Promise<Contract> {
            return fetch(abiUrl)
                .then(abi => abi.json())
                .then(abi => new web3.eth.Contract(abi, this.selectedSource.contractAddress));
        },

        transfer(): Promise<any> {
            return this.loadGoalboxContract()
                .then(contract => {
                    this.$emit("transfer-initiated")
                    return contract.methods.transfer(this.selectedTarget.contractAddress, Web3.utils.toWei(this.selectedAmount.toString()))
                        .send({ from: this.accountAddress });
                });
        },
    },

    mounted() {
        if (!this.source) {
            return;
        }

        this.selectedSource = this.source;
    },
})
</script>
