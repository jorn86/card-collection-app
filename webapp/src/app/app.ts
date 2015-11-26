///<reference path="menu.ts"/>
///<reference path="versioncheck.ts"/>

import {bootstrap, Component} from 'angular2/angular2';
import {RouteConfig, ROUTER_DIRECTIVES, ROUTER_PROVIDERS} from 'angular2/router';
import {MenuComponent} from "./menu";
import {VersionCheckComponent} from "./versioncheck";

@RouteConfig([
    //{ path: '/deck/:id', component: DeckComponent},
    //{ path: '/inventory', component: InventoryComponent},
    { path: '/versioncheck', component: VersionCheckComponent},
])
@Component({
    selector: 'card-app',
    templateUrl: 'app/main.html',
    directives: [MenuComponent]
})
class CardAppComponent { }

bootstrap(CardAppComponent);
