function [OK] = xyzread(sheet)
% Import SwissTopo xyz DHM File
%
% bsp. 1091.xyz


infile=sprintf('../%i.xyz',sheet);
outfile=sprintf('%i.slope',sheet);

disp(infile)

[xyz]=dlmread(infile,' ',0,0);

x=xyz(:,1);
y=xyz(:,2);
z=xyz(:,3);


x=reshape(x,701,481);
y=reshape(y,701,481);
z=reshape(z,701,481);


[px,py]=gradient(z,25,25);

slope=sqrt(px.^2+py.^2);
grad=atan(slope).*360/(2*pi);


export=grad;
export=reshape(export,1,337181);

fid= fopen(outfile,'w');
fprintf(fid,'%3.2f\n',export);
fclose(fid);

