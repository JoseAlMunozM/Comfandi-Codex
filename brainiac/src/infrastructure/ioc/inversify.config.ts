import 'reflect-metadata';
import { Container } from 'inversify';
import { networkModule } from './containers/network/networkcontainer.module';
import { repositoryModule } from './containers/repositories/repositorycontainer.module';
import { usecasesModule } from './containers/usecases/usecasescontainer.module';
// import { servicesModule } from './containers/services/servicecontainer.module';

// Contenedor principal
const appContainer = new Container();
// appContainer.load(servicesModule);
appContainer.load(networkModule);
appContainer.load(repositoryModule);
appContainer.load(usecasesModule);
export { appContainer };
