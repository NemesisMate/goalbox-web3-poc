// SPDX-License-Identifier: MIT
pragma solidity ^0.8.13;

contract GoalboxContract {
    address payable public owner;
    bool settled;
    uint settledAmount;

    event Fund(address indexed from, uint value, uint balance);
    event Settle(uint value);
    event Transfer(address indexed to, uint value, uint balance);

    constructor (address _owner) payable {
        owner = payable(_owner);
    }

    modifier ownerOnly() {
        require(msg.sender == owner, 'Sender is not the owner of the Goalbox');
        _;
    }

    modifier nonSettled() {
        require(!settled, "Can't operate on a settled Goalbox");
        _;
    }

    receive() external nonSettled payable {
        emit Fund(msg.sender, msg.value, address(this).balance);
    }

    function fund() external nonSettled payable {
        emit Fund(msg.sender, msg.value, address(this).balance);
    }

    function settle() external ownerOnly nonSettled {
        uint amount = address(this).balance;
        (bool success, ) = owner.call{value: amount}("");

        require(success, "Failed to settle Goalbox");

        settledAmount = amount;
        settled = true;

        emit Settle(amount);
    }

    function transfer(address payable to, uint amount) external ownerOnly nonSettled {
        (bool success, ) = to.call{value: amount}("");

        require(success, "Failed to transfer Wei to another Goalbox");

        emit Transfer(to, amount, address(this).balance);
    }

    function getValue() external view returns (uint) {
        return settled ? settledAmount : address(this).balance;
    }

    function isSettled() external view returns (bool) {
        return settled;
    }

}
