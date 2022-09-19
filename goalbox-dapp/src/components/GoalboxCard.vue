<template>
    
    <v-card class="ma-2">

        <v-card-title>
                {{goalbox.name}}
            <v-tooltip>
                <template v-slot:activator="{ props }">
                    <v-icon size="x-small" v-bind="props" @click="copy(goalbox.contractAddress)">mdi-information</v-icon>
                </template>
                <span>{{goalbox.contractAddress}}</span>
            </v-tooltip>
            <v-tooltip v-if="lastOperation">
                <template v-slot:activator="{ props }">
                    <v-icon size="x-small" v-bind="props">mdi-check-outline</v-icon>
                </template>
                <span>{{lastOperation}}</span>
            </v-tooltip>
        </v-card-title>

        <v-card-text class="ma-2">
            <v-row align="center">
                <v-col cols="1">
                    <v-icon>mdi-piggy-bank</v-icon>
                </v-col>
                <v-col class="text-h5" cols="10">
                    <strong>{{value}} ETH</strong>
                </v-col>
            </v-row>
            <v-row justify="center">
                <v-col>
                    <v-progress-linear v-model="progress" rounded height="13" ></v-progress-linear>
                </v-col>
            </v-row>
            <v-row justify="end">
                <v-col mx="auto" align="end">
                    <p class="text-disabled">Goal: {{goal}} ETH</p>
                </v-col>
            </v-row>
        </v-card-text>

        <v-divider></v-divider>

        <v-card-actions>
            <v-spacer></v-spacer>

            <v-btn variant="outlined" @click.stop="dialogFund = true">FUND</v-btn>
            <v-btn variant="outlined" @click.stop="settle()">SETTLE</v-btn>
            <v-btn variant="outlined" @click.stop="openTransfer()">TRANSFER</v-btn>

            <v-spacer></v-spacer>
        </v-card-actions>

        <v-dialog v-model="dialogFund">
            <v-card>
                <v-card-title class="text-h5">
                    Add funds to this goal box?
                </v-card-title>

                <v-card-text>
                    <v-container>
                        <v-row justify="center">
                            <v-text-field label="Amount" type="number" suffix="ETH" required v-model="fundAmount">
                            </v-text-field>
                        </v-row>
                    </v-container>
                </v-card-text>

                <v-card-actions>
                    <v-spacer></v-spacer>
                    <v-btn color="primary" variant="outlined" text @click="dialogFund = false; fund(fundAmount)">Fund
                    </v-btn>
                </v-card-actions>
            </v-card>
        </v-dialog>

        <v-overlay v-model="settled" contained :persistent="true" :close-on-back="false" :close-on-content-click="false"
            :close-on-hover="false" :disabled="true" class="align-center justify-center">
            {{goalReached ? "Succeeded" : "Failed"}}
        </v-overlay>
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

let EMPTY: Contract;
let web3: Web3;

export default defineComponent({
    name: 'GoalboxCard',

    setup() {
        let w3 = inject<Web3>(InjectionKeys.Web3);
        if (!w3?.eth) {
            throw Error();
        }
        web3 = w3;
    },

    props: {
        accountAddress: { type: String, required: true },
        goalbox: { type: Object as PropType<Goalbox>, required: true },
    },

    data() {
        return {
            contract: EMPTY,

            dialogFund: false,

            fundAmount: 1,
            settled: false,
            goalReached: false,
            transferAmount: 1,
            transferAccount: "",

            value: 0,
            goal: 0,
            progress: 0,

            lastOperation: "",
        }
    },

    methods: {
        loadGoalboxContract(): Promise<Contract> {
            if (!this.goalbox) {
                throw Error();
            }


            return fetch(abiUrl)
                .then(abi => abi.json())
                .then(abi => new web3.eth.Contract(abi, this.goalbox?.contractAddress));
        },

        fund(amount: number): Promise<any> {
            if (amount < 0) {
                return Promise.resolve();
            }

            return this.contract.methods.fund().send({ from: this.accountAddress, value: Web3.utils.toWei(amount.toString(), "ether") });
        },

        settle(): Promise<any> {
            return this.contract.methods.settle().send({ from: this.accountAddress });
        },

        updateValue(value: string) {
            this.value = this.toEther(value);
            this.updateProgress();
        },

        listenToContract() {
            this.contract.events.allEvents({}, (error: any, event: any) => {
                this.updateValue(event.returnValues.balance);
                this.lastOperation = `Added funds: ${this.toEther(event.returnValues.value)} ETH`;
            });

            this.contract.events.Fund({}, (error: any, event: any) => {
                this.updateValue(event.returnValues.balance);
                this.lastOperation = `Added funds: ${this.toEther(event.returnValues.value)} ETH`;
            });

            this.contract.events.Transfer({}, (error: any, event: any) => {
                this.updateValue(event.returnValues.balance);
                this.lastOperation = `Transferred out: ${this.toEther(event.returnValues.value)} ETH`;
            });

            this.contract.events.Settle({}, (error: any, event: any) => {
                this.settled = true;
                this.lastOperation = "";
                this.updateValue(event.returnValues.value);
            });
        },

        openTransfer() {
            this.$emit('open-transfer');
        },

        updateProgress() {
            this.progress = Math.min(100, this.goal ? (this.value / this.goal) * 100 : 0);
            this.goalReached = this.value >= this.goal || this.value >= this.goal;
        },

        toEther(amount: string | BigInt): number {
            return Number(Web3.utils.fromWei(amount.toString(), "ether"))
        },

        copy(text: string) {
            navigator.clipboard.writeText(text);
        },
    },

    mounted() {
        this.goal = this.toEther(this.goalbox.goal);
        this.settled = this.goalbox.settled;

        this.updateValue(this.goalbox.value.toString());

        this.loadGoalboxContract()
            .then(contract => {
                this.contract = contract;
                this.listenToContract();
            })
    },

    watch: {
        balance(now: string, before: string) {
            this.updateProgress();
        }
    },

})
</script>
