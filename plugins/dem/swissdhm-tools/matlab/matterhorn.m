% Import SwissTopo DHM File
%
% 1091.xyz


[xyz]=dlmread('mmma25.xyz',' ',0,0);

xyz=xyz(:,1:3);



% normale 3D
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
x=xyz(:,1);
y=xyz(:,2);
z=xyz(:,3);

%plot3(x,y,z)



% Dichte Funktionen
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
xm=reshape(x,81,81);
ym=reshape(y,81,81);
zm=reshape(z,81,81);

%surface(xm,ym,zm)
%colorbar

%contourf(xm,ym,zm,40)
%colorbar

%%mesh(xm,ym,zm)
%surf(xm,ym,zm)
%colorbar



% Gradient
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
[px,py]=gradient(zm,25,25);

%quiver(px,py)
%hold on
%contour(zm,30)
%hold off

slope=sqrt(px.^2+py.^2);
grad=atan(slope).*360/(2*pi);

surface(xm,ym,grad)
colorbar


