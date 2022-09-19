ARG contracts=goalbox-contract
ARG module=goalbox-dapp

FROM nemesismate/solc-node-yarn:v0.8.17_14.20.0 AS build

ARG contracts
ARG module

COPY ${contracts} ${contracts}
COPY ${module} ${module}

WORKDIR ${module}

RUN yarn install --frozen-lockfile
RUN yarn run build


FROM nginx:1.23.1

ARG module

COPY --from=build ${module}/dist /usr/share/nginx/html