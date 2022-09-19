export interface Goalbox {
    id: string;
    name: string;
    ownerAddress: string;
    contractAddress: string;
    value: bigint;
    goal: bigint;
    settled: boolean;
}
