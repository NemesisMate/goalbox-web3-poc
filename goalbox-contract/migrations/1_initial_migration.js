var GoalboxContract = artifacts.require("GoalboxContract");

module.exports = function(deployer) {
  deployer.deploy(GoalboxContract, "0x3dEE3252C6B096d902BD82c56441fBE01e925A23");
};